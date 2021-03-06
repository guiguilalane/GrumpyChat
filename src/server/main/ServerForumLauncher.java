package server.main;

import java.net.BindException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

import server.objects.ServerForum;

/**
 * The server launcher
 * 
 * @author Grumpy Group
 */
public class ServerForumLauncher {

	/**
	 * Allow to launch the server
	 * 
	 * @param args
	 * @throws java.net.UnknownHostException
	 * @throws RemoteException
	 */
	public static void main(String[] args) {
		int port = 1100;
		try {
			System.out.println("[ Trying registry with port " + port + " ]");

			LocateRegistry.createRegistry(port);

			ServerForum serverForum = new ServerForum();
			serverForum.initializedDiscussions();
			/*
			 * On a dit pas rmi: !!
			 */
			String url = "//" + InetAddress.getLocalHost().getHostName() + ":"
					+ port + "/GrumpyChat";
			System.out.println("[ Registry of object with URL  ] : " + url);
			Naming.rebind(url, serverForum);

			System.out.println("[ GRUMPY CHAT ENGAGED ]");
		} catch (ExportException e) {
			if (e.getCause() instanceof BindException) {
				System.err
						.println("The server can not be started on this port: "
								+ port);
			}
			e.printStackTrace();
			System.exit(0);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
