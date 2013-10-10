package server.objects.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import client.implementation.ClientImplementation;



public interface ServerForumInterface extends Remote {

	/**
	 * Returns the DiscussionSubject identified by a string title, null otherwise
	 */
	public DiscussionSubjectInterface obtientSujet(String titre) 
			throws RemoteException;
	
	public String getList()
			throws RemoteException;

	public String display() 
			throws RemoteException;
	
	public boolean containsPseudo(String pseudo)
			throws RemoteException;
	
	public boolean subscribe(DiscussionSubjectInterface dsi, ClientImplementation client)
			throws RemoteException;

	public List<DiscussionSubjectInterface> getDiscussions()
			throws RemoteException;
	
	
}
