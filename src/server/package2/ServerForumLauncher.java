package server.package2;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import server.package3.ServerForum;

public class ServerForumLauncher {

	/**
	 * Allow to launch the server
	 * TODO : 
	 * @param args
	 * @throws java.net.UnknownHostException 
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws java.net.UnknownHostException {
		try {
			ServerForum forumServer = new ServerForum();
			System.out.println("[ Trying registry with port 1099 ]");

			LocateRegistry.createRegistry(1100);

			ServerForum serverForum = new ServerForum();
			String url = "rmi://" + InetAddress.getLocalHost().getHostName() + "/GrumpyChat";
			System.out.println("[ Registry of object with URL  ] : " + url);
			Naming.rebind(url, serverForum);

			System.out.println("[ GRUMPY CHAT ENGAGED ]");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 

	}
}
