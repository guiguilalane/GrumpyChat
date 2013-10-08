package server.package3;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;


public class ServerForum extends UnicastRemoteObject implements ServerForumInterface {

	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;
	private List<DiscussionSubjectInterface> discussionSubjects;
				
	
	/**
	 * Protected Constructor
	 * @throws RemoteException
	 */
	public ServerForum() throws RemoteException {
		super();		
		// TODO Auto-generated constructor stub
	}

	@Override
	public DiscussionSubjectInterface obtientSujet(String titre)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
