package server.objects;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import server.objects.interfaces.DiscussionSubjectInterface;
import server.objects.interfaces.ServerForumInterface;
import client.interfaces.ClientDisplayerInterface;

public class ServerMainForum extends ServerForum {

	/**
	 * ID
	 */
	private static final long serialVersionUID = 6541361995114344327L;
	private List<ServerRemoteForum> servers = new ArrayList<ServerRemoteForum>();
	private String ip;
	private int port;

	public ServerMainForum(String ip, int port) throws RemoteException {
		super();
		this.ip = ip;
		this.port = port;
	}

	protected boolean addServer(ServerRemoteForum server) {
		synchronized (this.servers) {
			return this.servers.add(server);
		}
	}

	protected boolean removeServer(ServerRemoteForum server) {
		synchronized (this.servers) {
			return this.servers.remove(server);
		}
	}

	protected ServerRemoteForum getServer(String url) {
		synchronized (this.servers) {
			for (ServerRemoteForum s : this.servers) {
				if (s.getRemoteURL().equalsIgnoreCase(url)) {
					return s;
				}
			}
		}
		return null;
	}

	@Override
	public DiscussionSubjectInterface create(ClientDisplayerInterface client,
			String subject) throws RemoteException {
		String remoteURL = "//" + this.ip + ":" + this.port + "/" + subject;
		if (this.getSubjectFromName(subject) != null
				|| this.isFullChannel(client) || this.serverExists(remoteURL)) {
			return null;
		}
//		ServerRemoteForum server = new ServerRemoteForum(remoteURL, this);
//		DiscussionSubjectInterface dsi = server.create(client, subject);
//		if (dsi != null) {
//			this.broadCastUpdateFrame(client);
//			dsi.subscribe(client);
//			this.addServer(server);
//			return dsi;
//		}
		return null;
	}

	private boolean serverExists(String url) {
		synchronized (this.servers) {
			for (ServerRemoteForum s : this.servers) {
				if (s.getRemoteURL().equalsIgnoreCase(url)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean subscribe(DiscussionSubjectInterface dsi,
			ClientDisplayerInterface client) throws RemoteException {
		ServerForumInterface server = null;
		if (this.containsSubject(dsi)) {
			return super.subscribe(dsi, client);
		} else {
			synchronized (this.servers) {
				for (ServerRemoteForum s : this.servers) {
					if (s.containsSubject(dsi)) {
						server = s;
						break;
					}
				}
			}
			if (server == null) {
				return false;
			}
		}
		return server.subscribe(dsi, client);
	}

	@Override
	public List<DiscussionSubjectInterface> getDiscussions()
			throws RemoteException {
		List<DiscussionSubjectInterface> subjects = new ArrayList<DiscussionSubjectInterface>();
		synchronized (this.discussionSubjects) {
			subjects.addAll(this.discussionSubjects);
			synchronized (this.servers) {
				for (ServerRemoteForum s : this.servers) {
					subjects.addAll(s.getDiscussions());
				}
			}
		}
		return subjects;
	}

	@Override
	public DiscussionSubjectInterface getSubjectFromName(String title)
			throws RemoteException {
		synchronized (this.discussionSubjects) {
			for (DiscussionSubjectInterface dsi : this.discussionSubjects) {
				if (dsi.getTitle().equalsIgnoreCase(title)) {
					return dsi;
				}
			}
		}
		synchronized (this.servers) {
			for (ServerRemoteForum s : this.servers) {
				DiscussionSubjectInterface dsi = s.getSubjectFromName(title);
				if (dsi != null) {
					return dsi;
				}
			}
		}
		return null;
	}

}
