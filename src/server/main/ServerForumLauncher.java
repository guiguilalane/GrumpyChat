package server.main;

import java.net.BindException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

import server.objects.ServerForum;

public class ServerForumLauncher {

	/**
	 * Allow to launch the server
	 * TODO : 
	 * @param args
	 * @throws java.net.UnknownHostException 
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws java.net.UnknownHostException {
		int port=1099;
		try {
			System.out.println("[ Trying registry with port "+port+" ]");

			LocateRegistry.createRegistry(port);

			ServerForum serverForum = new ServerForum();
			serverForum.initializedDiscutions();
			/*
			 * On a dit pas rmi: !!
			 */
			String url = "//" + InetAddress.getLocalHost().getHostName() + ":" +port +"/GrumpyChat";
			System.out.println("[ Registry of object with URL  ] : " + url);
			Naming.rebind(url, serverForum);

			System.out.println("[ GRUMPY CHAT ENGAGED ]");
		} catch(ExportException e) {
			if(e.getCause() instanceof BindException) {
				System.err.println("The server can not be started on this port: "+port);
				System.exit(0);
			}
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
