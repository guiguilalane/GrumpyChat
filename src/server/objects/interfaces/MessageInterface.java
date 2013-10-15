package server.objects.interfaces;

import java.rmi.Remote;
import java.util.Date;

import client.interfaces.ClientInterface;

/**
 * The message used in the discussion, identified by his user and the
 * message that he sent
 * @author Grumpy Group
 */
public interface MessageInterface extends Remote {
	/**
	 * Return the message string
	 * @return {@link String} - Message
	 */
	public String getMessage();
	/**
	 * Return the message author
	 * @return {@link ClientInterface} - The message sender
	 */
	public ClientInterface getClient();
	/**
	 * Return the date of message creation
	 * @return {@link Date} - The creation date
	 */
	public Date getDate();
	/**
	 * Return the date as {@link String} in format <code>HH:mm:ss</code>
	 * @return {@link String} - The date as {@link String} in format <code>HH:mm:ss</code>
	 */
	public String getDateString();
}
