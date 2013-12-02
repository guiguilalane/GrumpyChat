package server.objects;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import server.objects.interfaces.DiscussionSubjectInterface;
import server.objects.interfaces.MessageInterface;
import server.objects.interfaces.ServerForumInterface;
import client.interfaces.ClientDisplayerInterface;
import client.interfaces.ClientInterface;

/**
 * The server instance
 * 
 * @author Grumpy Group
 */
public class ServerForum extends UnicastRemoteObject implements
		ServerForumInterface {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -4777930444757298232L;
	/**
	 * The map of the {@link DiscussionSubjectInterface discussion subjects} and
	 * their {@link ClientDisplayerInterface authors}
	 */
	private List<DiscussionSubjectInterface> discussionSubjects = new ArrayList<DiscussionSubjectInterface>();
	/**
	 * The list of all connected {@link ClientDisplayerInterface users} on the
	 * forum
	 */
	private List<ClientDisplayerInterface> clients = new ArrayList<ClientDisplayerInterface>();

	/**
	 * Default Constructor
	 * 
	 * @throws RemoteException
	 */
	public ServerForum() throws RemoteException {
		super();
	}

	/**
	 * Creates a default discussion subject in the forum list
	 */
	public void initializedDiscussions() {
		try {
			synchronized (this.discussionSubjects) {
				this.discussionSubjects
						.add(new DiscussionSubject("Chat", null));
				this.discussionSubjects.add(new DiscussionSubject("Mangas",
						null));
				this.discussionSubjects.add(new DiscussionSubject("LoL", null));
				this.discussionSubjects.add(new DiscussionSubject(
						"Seeking for feeder", null));
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String display() {
		return this.toString();
	}

	@Override
	public boolean containsPseudo(String pseudo) {
		try {
			synchronized (this.clients) {
				for (ClientDisplayerInterface cdi : this.clients) {
					if (cdi.getClient().getPseudo().equalsIgnoreCase(pseudo)) {
						return true;
					}
				}
			}
		} catch (RemoteException e) {
			System.err.println("Error while searching pseudo in discussions");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<DiscussionSubjectInterface> getDiscussions()
			throws RemoteException {
		synchronized (this.discussionSubjects) {
			return this.discussionSubjects;
		}
	}

	@Override
	public int getNumberOfChannel(ClientDisplayerInterface client)
			throws RemoteException {
		int nbChannel = 0;
		synchronized (this.discussionSubjects) {
			for (DiscussionSubjectInterface dsi : this.discussionSubjects) {
				if (dsi.getOwner() != null && dsi.getOwner().equals(client)) {
					nbChannel++;
				}
			}
		}
		return nbChannel;
	}

	@Override
	public boolean isFullChannel(ClientDisplayerInterface client)
			throws RemoteException {
		return this.getNumberOfChannel(client) >= ServerForumInterface.CLIENT_MAX_CHANNEL;
	}

	@Override
	public boolean isChannelOwner(ClientDisplayerInterface client,
			String subject) throws RemoteException {
		DiscussionSubjectInterface dsi = this.getSubjectFromName(subject);
		if (dsi == null) {
			return false;
		}
		ClientDisplayerInterface cdi = dsi.getOwner();
		return cdi != null && cdi.equals(client);
	}

	@Override
	public String getOwner(DiscussionSubjectInterface dsi)
			throws RemoteException {
		/*
		 * If there is no discussions returns the server pseudo
		 */
		synchronized (this.discussionSubjects) {
			if (this.discussionSubjects.isEmpty()) {
				return ServerForumInterface.CLIENT.getPseudo();
			}
		}
		ClientDisplayerInterface cdi = dsi.getOwner();
		/*
		 * If the discussion does not have owner returns the server pseudo
		 */
		if (cdi == null) {
			return ServerForumInterface.CLIENT.getPseudo();
		}
		ClientInterface ci = cdi.getClient();
		ci = ci == null ? ServerForumInterface.CLIENT : ci;
		return ci.getPseudo();
	}

	@Override
	public DiscussionSubjectInterface remove(ClientDisplayerInterface client,
			String subject) throws RemoteException {
		DiscussionSubjectInterface dsi = this.getSubjectFromName(subject);
		/*
		 * If the client is not the discussion owner we leave
		 */
		if (dsi == null || !this.isChannelOwner(client, subject)) {
			return null;
		}
		/*
		 * Set all frames for connected users inactive
		 */
		synchronized (this.clients) {
			for (ClientDisplayerInterface cdi : this.clients) {
				if (!cdi.equals(client) && cdi.isOpenedDiscussion(dsi)) {
					cdi.setInactiveDiscussion(dsi);
				}
			}
		}
		/*
		 * Unsubscribe this user
		 */
		if (dsi.isConnected(client)) {
			this.unsubscribe(dsi, client);
		}
		/*
		 * Unsubscribe all other users in silent mode
		 */
		synchronized (this.clients) {
			for (ClientDisplayerInterface c : this.clients) {
				if (client == null || !client.equals(c)) {
					if (dsi.isConnected(c) && c.isOpenedDiscussion(dsi)) {
						this.unsubscribe(dsi, c, true);
					}
				}
			}
		}
		/*
		 * Send message to warn all other users that discussion is now closed
		 */
		this.broadCast(client, "The client '" + client.getClient().getPseudo()
				+ "' removed the channel '" + subject + "'");
		/*
		 * Remove the discussion from discussion list
		 */
		synchronized (this.discussionSubjects) {
			this.discussionSubjects.remove(dsi);
		}
		/*
		 * Update the main frame for all other users
		 */
		this.broadCastUpdateFrame(client);
		return dsi;
	}

	@Override
	public void closeFrames(ClientDisplayerInterface client,
			DiscussionSubjectInterface dsi) throws RemoteException {
		/*
		 * Can not make online actions in specific discussion frame
		 */
		synchronized (this.clients) {
			for (ClientDisplayerInterface cdi : this.clients) {
				if (!cdi.equals(client) && cdi.isOpenedDiscussion(dsi)) {
					cdi.setInactiveDiscussion(dsi);
				}
			}
		}
		/*
		 * Warn users that discussion has been removed
		 */
		synchronized (this.clients) {
			for (ClientDisplayerInterface cdi : this.clients) {
				if (!cdi.equals(client) && cdi.isOpenedDiscussion(dsi)) {
					cdi.closeDiscussionFrame(dsi);
					cdi.closeDiscussion(dsi);
				}
			}
		}
	}

	@Override
	public DiscussionSubjectInterface create(ClientDisplayerInterface client,
			String subject) throws RemoteException {
		if (this.getSubjectFromName(subject) != null
				|| this.isFullChannel(client)) {
			return null;
		}
		ClientInterface c = client.getClient();
		DiscussionSubjectInterface dsi = new DiscussionSubject(subject, client);
		boolean created = false;
		synchronized (this.discussionSubjects) {
			created = this.discussionSubjects.add(dsi);
		}
		if (created) {
			this.broadCast(client, "The client '" + c.getPseudo()
					+ "' created the channel '" + subject + "'");
			this.broadCastUpdateFrame(client);
			dsi.subscribe(client);
			dsi.addMessage(new Message(ServerForumInterface.CLIENT,
					"*** client '" + c.getPseudo()
							+ "' created the channel ***"));
			return dsi;
		}
		return null;
	}

	@Override
	public DiscussionSubjectInterface create(ClientDisplayerInterface client,
			String subject, String ip) throws RemoteException {
		if (this.getSubjectFromName(subject) != null
				|| this.isFullChannel(client)) {
			return null;
		}
		ClientInterface c = client.getClient();
		DiscussionSubjectInterface dsi = new DiscussionRemoteSubject(subject, client, ip);
		boolean created = false;
		synchronized (this.discussionSubjects) {
			created = this.discussionSubjects.add(dsi);
		}
		if (created) {
			this.broadCast(client, "The client '" + c.getPseudo()
					+ "' created the channel '" + subject + "'");
			this.broadCastUpdateFrame(client);
			dsi.subscribe(client);
			dsi.addMessage(new Message(ServerForumInterface.CLIENT,
					"*** client '" + c.getPseudo()
							+ "' created the channel ***"));
			return dsi;
		}
		return null;
	}

	@Override
	public void broadCastUpdateFrame(ClientDisplayerInterface client)
			throws RemoteException {
		synchronized (this.clients) {
			for (ClientDisplayerInterface c : this.clients) {
				if (client == null || !client.equals(c)) {
					c.updateChannelList(this.getDiscussions());
				}
			}
		}
	}

	@Override
	public boolean subscribe(DiscussionSubjectInterface dsi,
			ClientDisplayerInterface client) throws RemoteException {
		ClientInterface ci = client.getClient();
		boolean subscribed = dsi.subscribe(client);
		if (subscribed) {
			this.broadCast(client, "The client '" + ci.getPseudo()
					+ "' subscribed to channel '" + dsi.getTitle() + "'");
			synchronized (this.clients) {
				for (ClientDisplayerInterface c : this.clients) {
					if (!c.getClient().equals(ci) && dsi.isConnected(c)
							&& c.isOpenedDiscussion(dsi)) {
						c.newUser(ci, dsi);
					}
				}
			}
		} else {
			System.err.println("The client '" + ci.getPseudo()
					+ "' failed to subscribe to channel '" + dsi.getTitle()
					+ "'");
		}
		return subscribed;
	}

	@Override
	public boolean unsubscribe(DiscussionSubjectInterface dsi,
			ClientDisplayerInterface client) throws RemoteException {
		return this.unsubscribe(dsi, client, false);
	}

	private boolean unsubscribe(DiscussionSubjectInterface dsi,
			ClientDisplayerInterface client, boolean silent)
			throws RemoteException {
		ClientInterface ci = client.getClient();
		boolean unsubscribed = dsi.unsubscribe(client);
		if (unsubscribed) {
			if (!silent) {
				this.broadCast(client, "The client '" + ci.getPseudo()
						+ "' left the channel '" + dsi.getTitle() + "'");
			}
			synchronized (this.clients) {
				for (ClientDisplayerInterface c : this.clients) {
					if (!c.getClient().equals(ci) && dsi.isConnected(c)
							&& c.isOpenedDiscussion(dsi)) {
						c.leftUser(ci, dsi);
					}
				}
			}
		} else {
			System.err.println("The client '" + ci.getPseudo()
					+ "' failed to unsubscribe to channel '" + dsi.getTitle()
					+ "'");
		}
		return unsubscribed;
	}

	@Override
	public void askNewOwner(DiscussionSubjectInterface dsi)
			throws RemoteException {
		synchronized (this.clients) {
			for (ClientDisplayerInterface cdi : this.clients) {
				if (cdi.isOpenedDiscussion(dsi)) {
					cdi.setInactiveDiscussion(dsi);
				}
			}
		}
		synchronized (this.clients) {
			for (ClientDisplayerInterface cdi : this.clients) {
				if (cdi.isOpenedDiscussion(dsi)) {
					cdi.askNewOwner(dsi);
				}
			}
		}
		synchronized (this.clients) {
			dsi.setOwner(null);
			for (ClientDisplayerInterface c : this.clients) {
				if (dsi.isConnected(c) && c.isOpenedDiscussion(dsi)) {
					c.changeOwner(dsi);
				}
			}
		}
	}

	@Override
	public boolean setNewOwner(DiscussionSubjectInterface dsi,
			ClientDisplayerInterface client) throws RemoteException {
		boolean newOwner = false;
		synchronized (dsi) {
			if (dsi.getOwner() == null) {
				dsi.setOwner(client);
				this.broadCastUpdateFrame(client);
				synchronized (this.clients) {
					for (ClientDisplayerInterface c : this.clients) {
						if (dsi.isConnected(c) && c.isOpenedDiscussion(dsi)) {
							c.changeOwner(dsi);
						}
					}
				}
				newOwner = true;
			}
		}
		return newOwner;
	}

	@Override
	public ClientDisplayerInterface getClientController(ClientInterface ci)
			throws RemoteException {
		synchronized (this.clients) {
			for (ClientDisplayerInterface cdi : this.clients) {
				if (cdi.getClient().equals(ci)) {
					return cdi;
				}
			}
		}
		return null;
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
		return null;
	}

	@Override
	public boolean newUser(ClientDisplayerInterface client)
			throws RemoteException {
		synchronized (this.clients) {
			if (!this.clients.contains(client)) {
				this.clients.add(client);
				this.broadCast(client, "Client '"
						+ client.getClient().getPseudo()
						+ "' has joined the server");
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean disconnectUser(ClientDisplayerInterface client)
			throws RemoteException {
		synchronized (this.clients) {
			if (!this.clients.contains(client)) {
				return false;
			}
		}
		synchronized (this.discussionSubjects) {
			for (DiscussionSubjectInterface dsi : this.discussionSubjects) {
				if (dsi.isConnected(client)) {
					this.unsubscribe(dsi, client, true);
				}
			}
		}
		this.broadCast(client, "Client '" + client.getClient().getPseudo()
				+ "' has left the server");
		synchronized (this.clients) {
			return this.clients.remove(client);
		}
	}

	@Override
	public boolean addMessage(ClientDisplayerInterface client,
			DiscussionSubjectInterface discussion, MessageInterface msg)
			throws RemoteException {
		if (!discussion.addMessage(msg)) {
			return false;
		}
		client.getMessage(msg, discussion);
		synchronized (this.clients) {
			for (ClientDisplayerInterface cdi : this.clients) {
				if (client == null || !client.equals(cdi)) {
					if (discussion.isConnected(client)) {
						cdi.getMessage(msg, discussion);
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean broadCast(ClientDisplayerInterface client, String message)
			throws RemoteException {
		System.out.println(message);
		synchronized (this.clients) {
			for (ClientDisplayerInterface c : this.clients) {
				if (client == null || !client.equals(c)) {
					c.getMessage(message);
				}
			}
		}
		return true;
	}

	@Override
	public String getList() throws RemoteException {
		synchronized (this.discussionSubjects) {
			return Arrays.toString(this.discussionSubjects.toArray());
		}
	}

	@Override
	public String toString() {
		synchronized (this.discussionSubjects) {
			return "ServerForum [discussionSubjects="
					+ Arrays.toString(this.discussionSubjects.toArray()) + "]";
		}
	}
}
