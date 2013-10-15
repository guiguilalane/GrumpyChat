package client.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The client interface to use for user
 * @author Grumpy Group
 */
public interface ClientInterface extends Remote {

	/**
	 * Returns the client instance pseudo
	 * @return {@link String} - The client pseudo
	 * @throws RemoteException
	 */
	public String getPseudo() throws RemoteException;

}
