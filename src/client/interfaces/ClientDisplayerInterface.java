package client.interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * @author Niiner
 * Interface d'objet distant pour les afficheurs clients d'un sujet de discussion
 * objets localisés sur les sites clients du forum, captés sur le site serveur par références
 * passées en paramètre depuis les sites clients
 */
public interface ClientDisplayerInterface extends Remote{

	/**
	 * Display the message on the client's displayer
	 * @param message
	 */
	public void display(String message) throws RemoteException;
}
