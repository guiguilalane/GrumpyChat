package server.objects.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import client.interfaces.ClientDisplayerInterface;

/**
 * The server forum interface
 * @author Grumpy Group
 */
public interface ServerForumInterface extends Remote {

	/**
	 * Returns the DiscussionSubject identified by a string title, <code>null</code>
	 * otherwise
	 * @param title {@link String} - The discussion title
	 * @return {@link DiscussionSubjectInterface} - The discussion, <code>null</code>
	 * if does not exist
	 * @throws RemoteException
	 */
	public DiscussionSubjectInterface obtientSujet(String title) 
			throws RemoteException;
	
	/**
	 * Returns the discussion subjects list as {@link String}
	 * @return {@link String} - The discussion subjects list
	 * @throws RemoteException
	 */
	public String getList()
			throws RemoteException;

	/**
	 * Same as toString
	 * @see {@link server.objects.ServerForum#toString()}
	 */
	public String display() 
			throws RemoteException;
	
	/**
	 * <code>true</code> if the pseudo in parameters is already used
	 * in the {@link server.objects.ServerForum#clients connected users list}
	 * @param pseudo {@link String} - The pseudonyme
	 * @return {@link Boolean boolean} - <code>true</code> if this pseudo is already used,
	 * <code>false</code> otherwise
	 * @throws RemoteException
	 */
	public boolean containsPseudo(String pseudo)
			throws RemoteException;
	
	/**
	 * Send a request on the {@link DiscussionSubjectInterface discussion subject}
	 * to add this user in its list
	 * @param dsi {@link DiscussionSubjectInterface} - The discussion subject
	 * @param client {@link ClientDisplayerInterface} - The client to subscribe
	 * @return {@link Boolean boolean} - <code>true</code> if the subscription
	 * has been correctly done, <code>false</code> otherwise
	 * @throws RemoteException
	 */
	public boolean subscribe(DiscussionSubjectInterface dsi
			, ClientDisplayerInterface client) throws RemoteException;

	/**
	 * Returns the discussion subject list
	 * @return {@link DiscussionSubjectInterface} - The discussion subjects list
	 * @throws RemoteException
	 */
	public List<DiscussionSubjectInterface> getDiscussions()
			throws RemoteException;

	/**
	 * Create an user in the {@link server.objects.ServerForum#clients connected users list}
	 * @param client {@link ClientDisplayerInterface} - The client to add
	 * @return {@link Boolean boolean} - <code>true</code> if the client has been
	 * correctly added, <code>false</code> otherwise
	 * @throws RemoteException
	 */
	public boolean newUser(ClientDisplayerInterface client) 
			throws RemoteException;

	/**
	 * Send a message to all {@link server.objects.ServerForum#clients connected users},
	 * from a specific user who does not receive the message. It can be send by the
	 * forum server for all users with its {@link server.objects.ServerForum#client client instance}
	 * @param client {@link ClientDisplayerInterface} - The client who send the message
	 * @param message {@link String} - The message to send
	 * @return {@link Boolean boolean} - <code>true</code> if the message has been
	 * correctly sent to others users, <code>false</code> otherwise
	 * @throws RemoteException
	 */
	public boolean broadCast(ClientDisplayerInterface client, String message) 
			throws RemoteException;

	/**
	 * Create a {@link DiscussionSubjectInterface channel} in the forum list
	 * @param client {@link ClientDisplayerInterface} - The discussion subject author
	 * @param subject {@link String} - The channel name
	 * @return {@link DiscussionSubjectInterface} - The discussion subject if it has been
	 * created, <code>null</code> otherwise
	 * @throws RemoteException
	 */
	public DiscussionSubjectInterface create(ClientDisplayerInterface client,
			String subject) throws RemoteException;

	/**
	 * Returns the number of {@link DiscussionSubjectInterface channel} created by the
	 * specific {@link ClientDisplayerInterface user}
	 * @param client {@link DiscussionSubjectInterface} - The client displayer
	 * @return {@link Integer int} - The channels number
	 * @throws RemoteException
	 */
	public int getNumberOfChannel(ClientDisplayerInterface client)
			throws RemoteException;;

	/**
	 * Returns <code>true</code> if the specific {@link ClientDisplayerInterface user}
	 * has the maximum number of created {@link DiscussionSubjectInterface channel}
	 * @param client {@link DiscussionSubjectInterface} - The client displayer
	 * @return {@link Boolean boolean} - <code>true</code> if the client has already created
	 * the maximum of allowed channels, <code>false</code> otherwise
	 * @throws RemoteException
	 */
	public boolean isFullChannel(ClientDisplayerInterface client)
			throws RemoteException;
	
	/**
	 * Remove a {@link DiscussionSubjectInterface channel} in the forum list
	 * @param client {@link ClientDisplayerInterface} - The discussion subject author
	 * @param subject {@link String} - The channel name
	 * @return {@link DiscussionSubjectInterface} - The discussion subject if it has been
	 * removed, <code>null</code> otherwise
	 * @throws RemoteException
	 */
	public DiscussionSubjectInterface remove(ClientDisplayerInterface client, String subject)
			throws RemoteException;


	/**
	 * Returns <code>true</code> if the specific {@link ClientDisplayerInterface user}
	 * is the author of a specific {@link DiscussionSubjectInterface channel}
	 * @param client {@link DiscussionSubjectInterface} - The client displayer
	 * @param subject {@link String} - The channel name
	 * @return {@link Boolean boolean} - <code>true</code> if the client is the
	 * channel author, <code>false</code> otherwise
	 * @throws RemoteException
	 */
	public boolean isChannelOwner(ClientDisplayerInterface client,
			String subject) throws RemoteException;

}
