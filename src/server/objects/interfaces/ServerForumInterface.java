package server.objects.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import client.interfaces.ClientDisplayerInterface;



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
	
	public String getList()
			throws RemoteException;

	public String display() 
			throws RemoteException;
	
	public boolean containsPseudo(String pseudo)
			throws RemoteException;
	
	public boolean subscribe(DiscussionSubjectInterface dsi
			, ClientDisplayerInterface client) throws RemoteException;

	public List<DiscussionSubjectInterface> getDiscussions()
			throws RemoteException;

	public boolean newUser(ClientDisplayerInterface client) 
			throws RemoteException;

	public boolean broadCast(ClientDisplayerInterface client,String message) 
			throws RemoteException;

	public DiscussionSubjectInterface create(ClientDisplayerInterface client,
			String subject) throws RemoteException;

	public int getNumberOfChannel(ClientDisplayerInterface client)
			throws RemoteException;;

	public boolean isFullChannel(ClientDisplayerInterface client)
			throws RemoteException;

	public DiscussionSubjectInterface remove(ClientDisplayerInterface client, String subject)
			throws RemoteException;

	public boolean isChannelOwner(ClientDisplayerInterface client,
			String subject) throws RemoteException;

}
