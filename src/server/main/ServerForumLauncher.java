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
	 */
	public static void main(String[] args) {
		int port = 1100;
		String ip;
		try {
			ip = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return;
		}
//		String ip="192.168.1.66"; 
		try {
			System.out.println("[ Trying registry with port " + port + " ]");
			LocateRegistry.createRegistry(port);

			ServerForum serverForum = new ServerForum();
			serverForum.initializedDiscussions();
			/*
			 * On a dit pas 'rmi:'//... !!
			 */
			String url = "//"+ip+":"+port+"/GrumpyChat";
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
			System.exit(0);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
