package client.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

	public String getPseudo() throws RemoteException;

}
