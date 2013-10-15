package client.interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

import server.objects.interfaces.DiscussionSubjectInterface;

import client.gui.ClientMainFrame;
import client.implementation.ClientImplementation;


/**
 * @author Niiner
 * Interface d'objet distant pour les afficheurs clients d'un sujet de discussion
 * objets localis�s sur les sites clients du forum, capt�s sur le site serveur par r�f�rences
 * pass�es en param�tre depuis les sites clients
 */
public interface ClientDisplayerInterface extends Remote {
	
	public DiscussionSubjectInterface getCurrentDiscussion()
			throws RemoteException;

	void setCurrentDiscussion(DiscussionSubjectInterface currentDiscussion)
			throws RemoteException;

	/**
	 * Return the client of this interface
	 * @return {@link ClientInterface} - The client
	 * @throws RemoteException
	 */
	public ClientImplementation getClient() throws RemoteException;

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
	public ClientMainFrame getMainFrame() throws RemoteException;

	/**
	 * Receive a message from server
	 * @param message {@link Message} - The message
	 * @throws RemoteException
	 */
	public void getMessage(String message) throws RemoteException;

	public void exit() throws RemoteException;


}
