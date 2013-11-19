package server.objects;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;

import client.interfaces.ClientDisplayerInterface;

public class ServerRemoteForum extends ServerForum {

	/**
	 * ID
	 */
	private static final long serialVersionUID = 6541361995114344327L;
	private String remoteURL = null;

	public ServerRemoteForum(int port, String subject,
			List<ClientDisplayerInterface> clients) throws RemoteException {
		super();

		String url = null;
		try {
			url = "//" + InetAddress.getLocalHost().getHostAddress() + ":"
					+ port + "/"
					+ subject;
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		this.remoteURL = url;
		this.clients = clients;
		System.out.println("[ Registry of object with URL  ] : " + url);
		try {
			LocateRegistry.createRegistry(port);
			Naming.rebind(url, this);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	protected String getRemoteURL() {
		return this.remoteURL;
	}

}
