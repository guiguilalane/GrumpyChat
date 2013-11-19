package server.objects;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class ServerRemoteForum extends ServerForum {

	/**
	 * ID
	 */
	private static final long serialVersionUID = 6541361995114344327L;
	private String remoteURL=null;

	public ServerRemoteForum(String url) throws RemoteException {
		super();
		this.remoteURL=url;
		System.out.println("[ Registry of object with URL  ] : " + url);
		try {
			Naming.rebind(url, this);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	protected String getRemoteURL() {
		return this.remoteURL;
	}

}
