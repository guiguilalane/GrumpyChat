package server.objects;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Map<DiscussionSubjectInterface,ClientDisplayerInterface> discussionSubjects=
			new HashMap<DiscussionSubjectInterface,ClientDisplayerInterface>();
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
			this.discussionSubjects.put(new DiscussionSubject("Chat"),
					null);
			this.discussionSubjects.put(new DiscussionSubject("Mangas"),
					null);
			this.discussionSubjects.put(new DiscussionSubject("LoL"),
					null);
			this.discussionSubjects.put(new DiscussionSubject("Seeking for feeder"),
					null);
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
		return new ArrayList<DiscussionSubjectInterface>(
				this.discussionSubjects.keySet());
	}
	
	@Override
	public synchronized int getNumberOfChannel(ClientDisplayerInterface client) {
		int nbChannel=0;
		for(DiscussionSubjectInterface dsi:this.discussionSubjects.keySet()) {
			if(this.discussionSubjects.get(dsi)!=null&&
					this.discussionSubjects.get(dsi).equals(client)) {
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
		ClientDisplayerInterface cdi=this.discussionSubjects.get(dsi);
		return cdi!=null&&cdi.equals(client);
	}
	
	@Override
	public String getOwner(DiscussionSubjectInterface dsi)
			throws RemoteException {
		if(this.discussionSubjects.isEmpty()) {
			return ServerForumInterface.CLIENT.getPseudo();
		}
		ClientDisplayerInterface cdi=this.discussionSubjects.get(dsi);
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
	public void closeFrames(ClientDisplayerInterface client,
			DiscussionSubjectInterface dsi) throws RemoteException {
		for(ClientDisplayerInterface cdi:this.clients) {
			if(!cdi.equals(client)&&cdi.isOpenedDiscussion(dsi)) {
				cdi.closeDiscussionFrame(dsi);
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
		DiscussionSubjectInterface dsi=new DiscussionSubject(subject);
		boolean created = this.discussionSubjects.put(dsi,client)==null;
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
	public void broadCastUpdateFrame(ClientDisplayerInterface client)
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
		if(this.isChannelOwner(client, dsi.getTitle())) {
			this.discussionSubjects.put(dsi, null);
		}
		boolean unsubscribed = dsi.unsubscribe(ci);
		if(unsubscribed) {
			if(!silent) {
				this.broadCast(client, "The client '"+ci.getPseudo()+
						"' left the channel '"+dsi.getTitle()+"'");
			}
		}
		else {
			System.err.println("The client '"+ci.getPseudo()+
					"' failed to unsubscribe to channel '"+dsi.getTitle()+"'");
		}
		return unsubscribed;
	}

	@Override
	public synchronized DiscussionSubjectInterface getSubjectFromName(String title)
			throws RemoteException {
		for(DiscussionSubjectInterface dsi:this.discussionSubjects.keySet()) {
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
		for(DiscussionSubjectInterface dsi:this.discussionSubjects.keySet()) {
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
		return Arrays.toString(this.discussionSubjects.keySet().toArray());
	}

	@Override
	public synchronized String toString() {
		return "ServerForum [discussionSubjects=" +
				Arrays.toString(this.discussionSubjects.keySet().toArray())
				+ "]";
	}
}
