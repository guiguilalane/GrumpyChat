package client.implementation;

import java.io.Serializable;
import java.rmi.RemoteException;

import client.interfaces.ClientInterface;

/**
 * The client instance implementation, the client that is used for the user
 * 
 * @author Grumpy Chat
 */
public class ClientImplementation implements ClientInterface, Serializable {

	/**
	 * ID
	 */
	private static final long serialVersionUID = 1636115086769359904L;
	/**
	 * His identificator name
	 */
	private String pseudo = "Unamed";

	/**
	 * Constructor using his pseudo
	 * 
	 * @param pseudo
	 *            {@link String} - The client pseudo
	 */
	public ClientImplementation(String pseudo) {
		this.pseudo = pseudo;
	}

	@Override
	public String getPseudo() throws RemoteException {
		return this.pseudo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.pseudo == null) ? 0 : this.pseudo.hashCode());
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
		ClientImplementation other = (ClientImplementation) obj;
		if (this.pseudo == null) {
			if (other.pseudo != null)
				return false;
		} else if (!this.pseudo.equals(other.pseudo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Client['" + this.pseudo + "']";
	}
}
