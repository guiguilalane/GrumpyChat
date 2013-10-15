package client.interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

import server.objects.interfaces.DiscussionSubjectInterface;

import client.gui.ClientMainFrame;
import client.implementation.ClientImplementation;


/*
 * @author Niiner
 * Interface d'objet distant pour les afficheurs clients d'un sujet de discussion
 * objets localis�s sur les sites clients du forum, capt�s sur le site serveur par r�f�rences
 * pass�es en param�tre depuis les sites clients
 */
/**
 * Remote Objects interface to display the client displayers from the forum server.
 * @author Grumpy Group
 */
public interface ClientDisplayerInterface extends Remote {
	
	/**
	 * Returns the current discussion
	 * @return {@link DiscussionSubjectInterface} - The current discussion
	 */
	public DiscussionSubjectInterface getCurrentDiscussion()
			throws RemoteException;

	/**
	 * Define the current discussion
	 * @param currentDiscussion {@link DiscussionSubjectInterface} - The current
	 * discussion
	 * @throws RemoteException
	 */
	void setCurrentDiscussion(DiscussionSubjectInterface currentDiscussion)
			throws RemoteException;

	/**
	 * Return the client of this interface
	 * @return {@link ClientInterface} - The client
	 * @throws RemoteException
	 */
	public ClientImplementation getClient() throws RemoteException;

	/**
	 * Set the client instance for this user
	 * @param client {@link ClientImplementation} - The client instance
	 * @throws RemoteException
	 */
	void setClient(ClientImplementation client)
			throws RemoteException;

	/**
	 * Display the message on the client's displayer
	 * @param message
	 */
	public void display(String message, boolean inFrame) throws RemoteException;

	/**
	 * Display the error on the client's displayer
	 * @param message {@link String} - The error to display
	 * @throws RemoteException
	 */
	public void error(String message, boolean inFrame) throws RemoteException;
	
	/**
	 * The client main frame of graphic interface
	 * @return {@link ClientMainFrame} - The client main frame
	 * @throws RemoteException
	 */
	public ClientMainFrame getMainFrame() throws RemoteException;

	/**
	 * Receive a message from server
	 * @param message {@link String} - The message
	 * @throws RemoteException
	 */
	public void getMessage(String message) throws RemoteException;

	/**
	 * Close the server connection and exit interface
	 * @throws RemoteException
	 */
	public void exit() throws RemoteException;


}
