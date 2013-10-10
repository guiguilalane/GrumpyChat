package server.objects;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import server.objects.interfaces.DiscussionSubjectInterface;
import server.objects.interfaces.MessageInterface;
import client.interfaces.ClientInterface;


public class DiscussionSubject extends UnicastRemoteObject implements DiscussionSubjectInterface {
	
	/**
	 * ID
	 */
	private static final long serialVersionUID = -7970496351612327779L;
	/**
	 * The maximum of sulmutaneously connected clients;
	 */
	private int maxClients=10;
	/**
	 * The discussion title
	 */
	private String title="unknown";
	/**
	 * The list of registered clients
	 */
	private List<ClientInterface> clients=new ArrayList<ClientInterface>();
	/**
	 * The list of message in current discussion
	 */
	private List<MessageInterface> messages=new ArrayList<MessageInterface>();
	
	/**
	 * Constructor with the discussion title
	 * @param title {@link String} - The discussion title
	 * @throws RemoteException
	 */
	public DiscussionSubject(String title) throws RemoteException {
		this.title=title;
	}
	
	@Override
	public String getTitle() throws RemoteException {
		return this.title;
	}

	@Override
	public List<ClientInterface> getClients() throws RemoteException {
		return this.clients;
	}

	@Override
	public List<MessageInterface> getMessages() throws RemoteException {
		return messages;
	}
	
	@Override
	public boolean addMessage(MessageInterface message) throws RemoteException {
		if(this.clients.contains(message.getClient())||
				message.getClient().getPseudo().equalsIgnoreCase("server"))
		{
			this.messages.add(message);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isFull() throws RemoteException {
		return this.clients.size()>=this.maxClients;
	}
	
	@Override
	public void setMaxClients(int max) throws RemoteException {
		this.maxClients=max;
	}

	@Override
	public boolean subscribe(ClientInterface client) throws RemoteException {
		if(!this.clients.contains(client)&&this.clients.size()<this.maxClients) {
			return this.clients.add(client);
		}
		return false;
	}

	@Override
	public boolean unsubscribe(ClientInterface client)
			throws RemoteException {
		return this.clients.remove(client);
	}
	
//	@Override
//	public void diffuse(String message) throws RemoteException {
//		// TODO Auto-generated method stub
//	}
	
	@Override
	public String getList() throws RemoteException {
		return Arrays.toString(this.clients.toArray());
	}
	
	@Override
	public boolean isConnected(ClientInterface client) throws RemoteException {
		return this.clients.contains(client);
	}

	@Override
	public String toString() {
		return "DiscussionSubject [title=" + title + ", clients: "+
				Arrays.toString(this.clients.toArray())+"]";
	}
}
