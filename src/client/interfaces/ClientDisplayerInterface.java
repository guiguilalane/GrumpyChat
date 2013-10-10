package client.interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;


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
}
