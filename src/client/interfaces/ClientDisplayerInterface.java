package client.interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

import client.implementation.ClientImplementation;


/**
 * @author Niiner
 * Interface d'objet distant pour les afficheurs clients d'un sujet de discussion
 * objets localis�s sur les sites clients du forum, capt�s sur le site serveur par r�f�rences
 * pass�es en param�tre depuis les sites clients
 */
public interface ClientDisplayerInterface extends Remote {

	/**
	 * Display the message on the client's displayer
	 * @param message
	 */
	public void display(String message) throws RemoteException;

	void start() throws RemoteException;

	/**
	 * Display the error on the client's displayer
	 * @param message {@link String} - The error to display
	 * @throws RemoteException
	 */
	void error(String message) throws RemoteException;

	/**
	 * Return the client of this interface
	 * @return {@link ClientInterface} - The client
	 * @throws RemoteException
	 */
	public ClientImplementation getClient() throws RemoteException;

	/**
	 * Receive a message from server
	 * @param message {@link Message} - The message
	 * @throws RemoteException
	 */
	void getMessage(String message) throws RemoteException;

//	/**
//	 * Same as equals function
//	 * @param client {@link ClientDisplayerInterface} - The displayer to compare
//	 * @return {@link Boolean boolean} - True if this and the object are the same
//	 * @throws RemoteException 
//	 */
//	public boolean isEquals(ClientDisplayerInterface client) throws RemoteException;
}
