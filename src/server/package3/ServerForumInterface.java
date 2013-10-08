package server.package3;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Niiner
 * Remote object interface for forumServer.
 * Unique object located on server site, 
 * known by an extern name : "//machine:port/leServeur" 
 */
public interface ServerForumInterface extends Remote{

	/**
	 * Returns the DiscussionSubject identified by a string title, null otherwise
	 */
	public DiscussionSubjectInterface obtientSujet(String titre) 
			throws RemoteException;

}
