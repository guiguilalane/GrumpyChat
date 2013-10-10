package client.implementation;

import java.io.Serializable;
import java.rmi.RemoteException;

import client.interfaces.ClientInterface;

public class ClientImplementation implements ClientInterface, Serializable {

	/**
	 * ID
	 */
	private static final long serialVersionUID = 1636115086769359904L;
	private String pseudo="Unamed";
	
	public ClientImplementation(String pseudo) {
		this.pseudo=pseudo;
	}

	@Override
	public String getPseudo() throws RemoteException {
		return this.pseudo;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pseudo == null) ? 0 : pseudo.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		System.err.println("Debug 1");
		if (this == obj)
			return true;
		System.err.println("Debug 2");
		if (obj == null)
			return false;
		System.err.println("Debug 3");
		if (getClass() != obj.getClass())
			return false;
		System.err.println("Debug 4");
		ClientImplementation other = (ClientImplementation) obj;
		if (pseudo == null) {
			System.err.println("Debug 5");
			if (other.pseudo != null)
				return false;
			System.err.println("Debug 6");
		} else if (!pseudo.equals(other.pseudo))
			return false;
		System.err.println("Debug 7");
		return true;
	}

	@Override
	public String toString() {
		return "Client['" + pseudo + "']";
	}
}
