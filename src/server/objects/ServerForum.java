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
 * @author Grumpy Group
 */
public class ServerForum extends UnicastRemoteObject implements ServerForumInterface {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -4777930444757298232L;
	/**
	 * The map of the {@link DiscussionSubjectInterface discussion subjects} and
	 * their {@link ClientDisplayerInterface authors}
	 */
	private List<DiscussionSubjectInterface> discussionSubjects=
			new ArrayList<DiscussionSubjectInterface>();
	/**
	 * The list of all connected {@link ClientDisplayerInterface users} on the forum
	 */
	private List<ClientDisplayerInterface> clients=
			new ArrayList<ClientDisplayerInterface>();
	
	/**
	 * Default Constructor
	 * @throws RemoteException
	 */
	public ServerForum() throws RemoteException {
		super();
	}

	/**
	 * Creates a default discussion subject in the forum list
	 */
	public void initializedDiscussions()
	{
		try {
			this.discussionSubjects.add(new DiscussionSubject("Chat",
					null));
			this.discussionSubjects.add(new DiscussionSubject("Mangas",
					null));
			this.discussionSubjects.add(new DiscussionSubject("LoL",
					null));
			this.discussionSubjects.add(new DiscussionSubject("Seeking for feeder",
					null));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String display() {
		return this.toString();
	}
	
	@Override
	public synchronized boolean containsPseudo(String pseudo) {
		try {
			for(ClientDisplayerInterface ci:this.clients) {
				if(ci.getClient().getPseudo().equalsIgnoreCase(pseudo)) {
					return true;
				}
			}
		} catch (RemoteException e) {
			System.err.println("Error while searching pseudo in discussions");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public synchronized List<DiscussionSubjectInterface> getDiscussions()
			throws RemoteException {
		return this.discussionSubjects;
	}
	
	@Override
	public synchronized int getNumberOfChannel(ClientDisplayerInterface client)
			throws RemoteException {
		int nbChannel=0;
		for(DiscussionSubjectInterface dsi:this.discussionSubjects) {
			if(dsi.getOwner()!=null&&dsi.getOwner().equals(client)) {
				nbChannel++;
			}
		}
		return nbChannel;
	}
	
	@Override
	public synchronized boolean isFullChannel(ClientDisplayerInterface client)
			throws RemoteException {
		return this.getNumberOfChannel(client)>=ServerForum.CLIENT_MAX_CHANNEL;
	}
	
	@Override
	public synchronized boolean isChannelOwner(ClientDisplayerInterface client,
			String subject) throws RemoteException {
		DiscussionSubjectInterface dsi=this.getSubjectFromName(subject);
		ClientDisplayerInterface cdi=dsi.getOwner();
		return cdi!=null&&cdi.equals(client);
	}
	
	@Override
	public String getOwner(DiscussionSubjectInterface dsi)
			throws RemoteException {
		if(this.discussionSubjects.isEmpty()) {
			return ServerForumInterface.CLIENT.getPseudo();
		}
		ClientDisplayerInterface cdi=dsi.getOwner();
		if(cdi==null) {
			return ServerForumInterface.CLIENT.getPseudo();
		}
		ClientInterface ci=cdi.getClient();
		ci=ci==null?ServerForumInterface.CLIENT:ci;
		return ci.getPseudo();
	}
	
	@Override
	public synchronized DiscussionSubjectInterface remove(ClientDisplayerInterface client,
			String subject) throws RemoteException {
		DiscussionSubjectInterface dsi=this.getSubjectFromName(subject);
		if(dsi==null||!this.isChannelOwner(client, subject)) {
			return null;
		}
		this.unsubscribe(dsi, client);
		for(ClientDisplayerInterface c:this.clients) {
			if(client==null||!client.equals(c)) {
				if(dsi.isConnected(c.getClient())&&c.isOpenedDiscussion(dsi)) {
					this.unsubscribe(dsi, c, true);
				}
			}
		}
		this.broadCast(client, "The client '"+client.getClient().getPseudo()+
				"' removed the channel '"+subject+"'");
		this.discussionSubjects.remove(dsi);
		this.broadCastUpdateFrame(client);
		return dsi;
	}
	
	@Override
	public synchronized void closeFrames(ClientDisplayerInterface client,
			DiscussionSubjectInterface dsi) throws RemoteException {
		for(ClientDisplayerInterface cdi:this.clients) {
			if(!cdi.equals(client)&&cdi.isOpenedDiscussion(dsi)) {
				cdi.setInactiveDiscussion(dsi);
			}
		}
		for(ClientDisplayerInterface cdi:this.clients) {
			if(!cdi.equals(client)&&cdi.isOpenedDiscussion(dsi)) {
				cdi.closeDiscussionFrame(dsi);
			}
		}
		for(ClientDisplayerInterface cdi:this.clients) {
			if(!cdi.equals(client)&&cdi.isOpenedDiscussion(dsi)) {
				cdi.disposeDiscussionFrame(dsi);
			}
		}
	}

	@Override
	public synchronized DiscussionSubjectInterface create(ClientDisplayerInterface client,
			String subject) throws RemoteException {
		if(this.getSubjectFromName(subject)!=null||
				this.isFullChannel(client)) {
			return null;
		}
		ClientInterface c=client.getClient();
		DiscussionSubjectInterface dsi=new DiscussionSubject(subject, client);
		boolean created = this.discussionSubjects.add(dsi);
		if(created) {
			this.broadCast(client, "The client '"+c.getPseudo()+
					"' created the channel '"+subject+"'");
			this.broadCastUpdateFrame(client);
			dsi.subscribe(c);
			dsi.addMessage(new Message(ServerForum.CLIENT, "*** client '"+c.getPseudo()+
					"' created the channel ***"));
			return dsi;
		}
		return null;
	}
	
	@Override
	public synchronized void broadCastUpdateFrame(ClientDisplayerInterface client)
			throws RemoteException {
		for(ClientDisplayerInterface c:this.clients) {
			if(client==null||!client.equals(c)) {
				c.updateChannelList(this.getDiscussions());
			}
		}
	}

	@Override
	public synchronized boolean subscribe(DiscussionSubjectInterface dsi,
			ClientDisplayerInterface client) throws RemoteException {
		ClientInterface ci=client.getClient();
		boolean subscribed = dsi.subscribe(ci);
		if(subscribed) {
			this.broadCast(client, "The client '"+ci.getPseudo()+
					"' subscribed to channel '"+dsi.getTitle()+"'");
			for(ClientDisplayerInterface c:this.clients) {
				if(!c.getClient().equals(ci)&&dsi.isConnected(c.getClient())&&
						c.isOpenedDiscussion(dsi)) {
					c.newUser(ci,dsi);
				}
			}
		}
		else {
			System.err.println("The client '"+ci.getPseudo()+
					"' failed to subscribe to channel '"+dsi.getTitle()+"'");
		}
		return subscribed;
	}
	
	@Override
	public synchronized boolean unsubscribe(DiscussionSubjectInterface dsi,
			ClientDisplayerInterface client) throws RemoteException {
		return this.unsubscribe(dsi, client, false);
	}
	
	private synchronized boolean unsubscribe(DiscussionSubjectInterface dsi,
			ClientDisplayerInterface client, boolean silent) throws RemoteException {
		ClientInterface ci=client.getClient();
		boolean unsubscribed = dsi.unsubscribe(ci);
		if(unsubscribed) {
			if(!silent) {
				this.broadCast(client, "The client '"+ci.getPseudo()+
						"' left the channel '"+dsi.getTitle()+"'");
			}
			for(ClientDisplayerInterface c:this.clients) {
				if(!c.getClient().equals(ci)&&dsi.isConnected(c.getClient())&&
						c.isOpenedDiscussion(dsi)) {
					c.leftUser(ci,dsi);
				}
			}
		}
		else {
			System.err.println("The client '"+ci.getPseudo()+
					"' failed to unsubscribe to channel '"+dsi.getTitle()+"'");
		}
		return unsubscribed;
	}
	
	@Override
	public synchronized boolean askNewOwner(DiscussionSubjectInterface dsi) throws RemoteException {
		boolean changed=false;
		for(ClientInterface ci:dsi.getClients()) {
			if(dsi.getOwner()!=null&&dsi.getOwner().getClient().equals(ci)) {
				continue;
			}
			ClientDisplayerInterface cdi=this.getClientController(ci);
			changed=cdi.askNewOwner(dsi);
			if(changed) {
				dsi.setOwner(cdi);
				break;
			}
		}
		if(!changed) {
			dsi.setOwner(null);
		}
		for(ClientDisplayerInterface c:this.clients) {
			if(dsi.isConnected(c.getClient())&&c.isOpenedDiscussion(dsi)) {
				c.changeOwner(dsi);
			}
		}
		return changed;
	}

	@Override
	public synchronized ClientDisplayerInterface getClientController(ClientInterface ci)
			throws RemoteException {
		for(ClientDisplayerInterface cdi:this.clients) {
			if(cdi.getClient().equals(ci)) {
				return cdi;
			}
		}
		return null;
	}

	@Override
	public synchronized DiscussionSubjectInterface getSubjectFromName(String title)
			throws RemoteException {
		for(DiscussionSubjectInterface dsi:this.discussionSubjects) {
			if(dsi.getTitle().equalsIgnoreCase(title)) {
				return dsi;
			}
		}
		return null;
	}
	
	@Override
	public synchronized boolean newUser(ClientDisplayerInterface client)
			throws RemoteException {
		if(!this.clients.contains(client)) {
			this.clients.add(client);
			this.broadCast(client,"Client '"+client.getClient().getPseudo()+
					"' has joined the server");
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized boolean disconnectUser(ClientDisplayerInterface client)
			throws RemoteException {
		if(!this.clients.contains(client)) {
			return false;
		}
		for(DiscussionSubjectInterface dsi:this.discussionSubjects) {
			if(dsi.isConnected(client.getClient())) {
				this.unsubscribe(dsi, client, true);
			}
		}
		this.broadCast(client,"Client '"+client.getClient().getPseudo()+
				"' has left the server");
		return this.clients.remove(client);
	}
	
	@Override
	public synchronized boolean addMessage(ClientDisplayerInterface client, DiscussionSubjectInterface discussion, 
			MessageInterface msg) throws RemoteException {
		if(!discussion.addMessage(msg)) {
			return false;
		}
		client.getMessage(msg,discussion);
		for(ClientDisplayerInterface c:this.clients) {
			if(client==null||!client.equals(c)) {
				if(discussion.isConnected(client.getClient())) {
					c.getMessage(msg,discussion);
				}
			}
		}
		return true;
	}
	
	@Override
	public synchronized boolean broadCast(ClientDisplayerInterface client, String message)
			throws RemoteException {
		System.out.println(message);
		for(ClientDisplayerInterface c:this.clients) {
			if(client==null||!client.equals(c)) {
				c.getMessage(message);
			}
		}
		return true;
	}

	@Override
	public synchronized String getList() throws RemoteException {
		return Arrays.toString(this.discussionSubjects.toArray());
	}

	@Override
	public synchronized String toString() {
		return "ServerForum [discussionSubjects=" +
				Arrays.toString(this.discussionSubjects.toArray())
				+ "]";
	}
}
