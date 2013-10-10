package server.objects.interfaces;

import java.rmi.Remote;
import java.util.Date;

import client.interfaces.ClientInterface;

public interface MessageInterface extends Remote {
	public String getMessage();
	public ClientInterface getClient();
	public Date getDate();
	public String getDateString();
}
