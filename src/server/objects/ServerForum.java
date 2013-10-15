package server.objects;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.objects.interfaces.DiscussionSubjectInterface;
import server.objects.interfaces.ServerForumInterface;
import client.implementation.ClientImplementation;
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
	 * The server client instance, used to send message for example
	 */
	private ClientInterface client = new ClientImplementation("SERVER");
	/**
	 * Set the maximum of {@link DiscussionSubjectInterface channels} that a user
	 * can create
	 */
	private static final int CLIENT_MAX_CHANNEL=3;
	
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
		DiscussionSubjectInterface dsi=this.obtientSujet(subject);
		ClientDisplayerInterface cdi=this.discussionSubjects.get(dsi);
		return cdi!=null&&cdi.equals(client);
	}
	
	@Override
	public synchronized DiscussionSubjectInterface remove(ClientDisplayerInterface client,
			String subject) throws RemoteException {
		if(this.obtientSujet(subject)==null||
				!this.isChannelOwner(client, subject)) {
			return null;
		}
		DiscussionSubjectInterface dsi=this.obtientSujet(subject);
		this.discussionSubjects.remove(this.obtientSujet(subject));
		return dsi;
	}

	@Override
	public synchronized DiscussionSubjectInterface create(ClientDisplayerInterface client,
			String subject) throws RemoteException {
		if(this.obtientSujet(subject)!=null||
				this.isFullChannel(client)) {
			return null;
		}
		ClientInterface c=client.getClient();
		DiscussionSubjectInterface dsi=new DiscussionSubject(subject);
		boolean created = this.discussionSubjects.put(dsi,client)==null;
		if(created) {
			this.broadCast(client, "The client '"+c.getPseudo()+
					"' created the channel '"+subject+"'");
			dsi.subscribe(c);
			dsi.addMessage(new Message(this.client, "*** client '"+c.getPseudo()+
					"' created the channel ***"));
			return dsi;
		}
		return null;
	}
	
	@Override
	public synchronized boolean subscribe(DiscussionSubjectInterface dsi,
			ClientDisplayerInterface client) throws RemoteException {
		ClientInterface c=client.getClient();
		boolean subscribed = dsi.subscribe(c);
		if(subscribed) {
			this.broadCast(client, "The client '"+c.getPseudo()+
					"' subscribed to channel '"+dsi.getTitle()+"'");
			dsi.addMessage(new Message(this.client, "*** client '"+c.getPseudo()+
					"' is now connected on this channel ***"));
		}
		else {
			System.err.println("The client '"+c.getPseudo()+
					"' failed to subscribe to channel '"+dsi.getTitle()+"'");
		}
		return subscribed;
	}

	@Override
	public synchronized DiscussionSubjectInterface obtientSujet(String title)
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
