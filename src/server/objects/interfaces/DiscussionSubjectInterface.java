package server.objects.interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import client.interfaces.ClientInterface;

/**
 * A remote object interface for all discussion subject (sport, cinema, music, ...)
 * @author Grumpy Group
 */
public interface DiscussionSubjectInterface extends Remote {
	
	/**
	 * Return the discussion title
	 * @return {@link String} - Discussion title
	 * @throws RemoteException
	 */
	public String getTitle() throws RemoteException;
	
	/**
	 * Return this clients list registered on this discussion
	 * @return {@link List}<{@link ClientInterface}> - The clients list
	 * @throws RemoteException
	 */
	public List<ClientInterface> getClients() throws RemoteException;
	
	/**
	 * Return the clients list registered on this discussion in {@link String} format
	 * @return {@link String} - The client list
	 * @throws RemoteException
	 */
	public String getList() throws RemoteException;
	
	/**
	 * Return true if the client in parameter is connected on this discussion
	 * @param client {@link ClientInterface} - The client to search in the registered clients list
	 * @return {@link Boolean boolean} - <code>true</code> if the client is registered on this
	 * discussion, <code>false</code> otherwise
	 * @throws RemoteException
	 */
	public boolean isConnected(ClientInterface client) throws RemoteException;
	
	/**
	 * Subscribe a {@link ClientInterface Client} to a discussion subject
	 * @param client {@link ClientInterface} - The client to register in this discussion
	 * @return {@link Boolean boolean} - <code>true</code> if client has been correctly added,
	 * <code>false</code> otherwise
	 * @throws RemoteException
	 */
	public boolean subscribe(ClientInterface client)
		throws RemoteException;
	
	/**
	 * Unsubscribe a {@link ClientInterface Client} to a discussion subject
	 * @param client {@link ClientInterface} - The client to unregister in this discussion
	 * @return {@link Boolean boolean} - <code>true</code> if the client has been correctly removed,
	 * <code>false</code> otherwise
	 * @throws RemoteException
	 */
	public boolean unsubscribe(ClientInterface client)
		throws RemoteException;

	/**
	 * Return true if the maximum of clients in this discussion is reached
	 * @return {@link Boolean boolean} - <code>true</code> if the channel is full,
	 * <code>false</code> otherwise
	 * @throws RemoteException 
	 */
	public boolean isFull() throws RemoteException;
	
	/**
	 * Set the maximum of simultaneous connected clients
	 * @param max {@link Integer int} - The max number of clients
	 * @throws RemoteException 
	 */
	public void setMaxClients(int max) throws RemoteException;

	/**
	 * Return the messages of this discussion
	 * @return {@link List}<{@link MessageInterface}> - The messages list
	 * @throws RemoteException
	 */
	public List<MessageInterface> getMessages() throws RemoteException;

	/**
	 * Add a message in discussion messages list
	 * @param message {@link MessageInterface} - The message to add
	 * @return {@link Boolean boolean} - <code>true</code> if the message has been
	 * correctly added, <code>false</code> otherwise
	 * @throws RemoteException
	 */
	public boolean addMessage(MessageInterface message) throws RemoteException;
	
//	/**
//	 * Diffuse the message to all DisplayerClient which subscribe to a subject
//	 * @param message The message to display
//	 * @throws RemoteException
//	 */
//	public void diffuse(String message)
//		throws RemoteException;
}
