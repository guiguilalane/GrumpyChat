package server.package3;
import java.rmi.RemoteException;

import client.interfaces.ClientDisplayerInterface;


public class DiscussionSubject implements DiscussionSubjectInterface {

	@Override
	public void subscribe(ClientDisplayerInterface c) throws RemoteException {
		// TODO Auto-generated method stub
	}

	@Override
	public void unsubscribe(ClientDisplayerInterface c)
			throws RemoteException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void diffuse(String message) throws RemoteException {
		// TODO Auto-generated method stub
	}

}
