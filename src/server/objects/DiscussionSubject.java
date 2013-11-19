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
 * The discussion subjects on the forum
 * 
 * @author Grumpy Group
 */
public class DiscussionSubject extends UnicastRemoteObject implements
		DiscussionSubjectInterface {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -7970496351612327779L;
	/**
	 * The maximum of simultaneously connected clients;
	 */
	private int maxClients = 10;
	/**
	 * The discussion title
	 */
	private String title = "unknown";
	/**
	 * The list of registered clients
	 */
	private List<ClientDisplayerInterface> clients = new ArrayList<ClientDisplayerInterface>();
	/**
	 * The list of message in current discussion
	 */
	private List<MessageInterface> messages = new ArrayList<MessageInterface>();
	/**
	 * The discussion owner
	 */
	private ClientDisplayerInterface owner = null;
	/**
	 * The host server where it is deployed
	 */
	private ServerForumInterface server;

	/**
	 * Constructor with the discussion title
	 * 
	 * @param title
	 *            {@link String} - The discussion title
	 * @param owner
	 *            {@link ClientDisplayerInterface} - The discussion owner
	 *            controller
	 * @throws RemoteException
	 */
	public DiscussionSubject(String title, ClientDisplayerInterface owner,
			ServerForumInterface server) throws RemoteException {
		this.title = title;
		this.owner = owner;
		this.server = server;
	}

	@Override
	public ServerForumInterface getServer() throws RemoteException {
		return this.server;
	}

	@Override
	public String getTitle() throws RemoteException {
		return this.title;
	}

	@Override
	public List<ClientDisplayerInterface> getClients() throws RemoteException {
		synchronized (this.clients) {
			return this.clients;
		}
	}

	@Override
	public List<MessageInterface> getMessages() throws RemoteException {
		synchronized (this.messages) {
			return this.messages;
		}
	}

	@Override
	public ClientDisplayerInterface getOwner() throws RemoteException {
		return this.owner;
	}

	@Override
	public void setOwner(ClientDisplayerInterface owner) throws RemoteException {
		this.owner = owner;
	}

	private boolean containsClient(ClientInterface client)
			throws RemoteException {
		synchronized (this.clients) {
			for (ClientDisplayerInterface cdi : this.clients) {
				if (cdi.getClient().equals(client)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean addMessage(MessageInterface message) throws RemoteException {
		if (this.containsClient(message.getClient())
				|| message.getClient().equals(ServerForumInterface.CLIENT)) {
			synchronized (this.messages) {
				this.messages.add(message);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isFull() throws RemoteException {
		synchronized (this.clients) {
			return this.clients.size() >= this.maxClients;
		}
	}

	@Override
	public void setMaxClients(int max) throws RemoteException {
		this.maxClients = max;
	}

	@Override
	public boolean subscribe(ClientDisplayerInterface client)
			throws RemoteException {
		synchronized (this.clients) {
			if (!this.clients.contains(client)
					&& this.clients.size() < this.maxClients) {
				return this.clients.add(client);
			}
		}
		return false;
	}

	@Override
	public boolean unsubscribe(ClientDisplayerInterface client)
			throws RemoteException {
		synchronized (this.clients) {
			return this.clients.remove(client);
		}
	}

	@Override
	public void diffuse(MessageInterface message) throws RemoteException {
		// for(ClientDisplayerInterface cdi:this.clients) {
		// cdi.getMessage(message, this);
		// }
	}

	@Override
	public String getList() throws RemoteException {
		synchronized (this.clients) {
			return Arrays.toString(this.clients.toArray());
		}
	}

	@Override
	public boolean isConnected(ClientDisplayerInterface client)
			throws RemoteException {
		synchronized (this.clients) {
			return this.clients.contains(client);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + this.maxClients;
		result = prime * result
				+ ((this.title == null) ? 0 : this.title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscussionSubject other = (DiscussionSubject) obj;
		if (this.maxClients != other.maxClients)
			return false;
		if (this.title == null) {
			if (other.title != null)
				return false;
		} else if (!this.title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		synchronized (this.clients) {
			return "DiscussionSubject [title=" + this.title + ", clients: "
					+ Arrays.toString(this.clients.toArray()) + "]";
		}
	}
}
