package server.objects.interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

import client.interfaces.ClientDisplayerInterface;

/**
 * @author Niiner
 * A remote object interface for all discussion subject (sport, cinema, music, ...)
 */
public interface DiscussionSubjectInterface extends Remote{
	
	/**
	 * Subscribe a DisplayerClient to a discussion subject
	 * @param clientDisplayer The DisplayerClient
	 * @throws RemoteException
	 */
	public void subscribe(ClientDisplayerInterface clientDisplayer)
		throws RemoteException;
	
	/**
	 * Unsubscribe a DisplayerClient to a discussion subject
	 * @param clientDisplayer The DisplayerClient
	 * @throws RemoteException
	 */
	public void unsubscribe(ClientDisplayerInterface clientDisplayer)
		throws RemoteException;
	
	/**
	 * Diffuse the message to all DisplayerClient which subscribe to a subject
	 * @param message The message to display
	 * @throws RemoteException
	 */
	public void diffuse(String message)
		throws RemoteException;
}
