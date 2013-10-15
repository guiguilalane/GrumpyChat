package server.objects;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import server.objects.interfaces.MessageInterface;
import client.interfaces.ClientInterface;

/**
 * The message, identified by the #{@link ClientInterface client} and
 * his {@link String message}. 
 * @author Grumpy Group
 */
public class Message implements MessageInterface, Serializable {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -217825946119048920L;
	/**
	 * The client message's author
	 */
	private ClientInterface client;
	/**
	 * The message
	 */
	private String message;
	/**
	 * The message creation date
	 */
	private Date date;
	/**
	 * Date format as <code>HH:mm:ss</code>
	 */
	protected static SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");
	
	/**
	 * Constructor
	 * @param client {@link ClientInterface} - The client
	 * @param message {@link String} - The message
	 */
	public Message(ClientInterface client, String message) {
		super();
		this.date = Calendar.getInstance().getTime();
		this.message = message;
		this.client = client;
	}

	@Override
	public ClientInterface getClient() {
		return this.client;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public Date getDate() {
		return this.date;
	}
	
	@Override
	public String getDateString() {
		return "["+Message.dateFormat.format(this.date)+"] ";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (client == null) {
			if (other.client != null)
				return false;
		} else if (!client.equals(other.client))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Message [client=" + client + ", message=" + message + "]";
	}

}
