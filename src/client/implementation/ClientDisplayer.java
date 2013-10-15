package client.implementation;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import server.objects.interfaces.DiscussionSubjectInterface;
import client.gui.ClientMainFrame;
import client.interfaces.ClientDisplayerInterface;
import client.interfaces.ClientInterface;

/**
 * This class is the displayer that defines the clients attributes
 * @author Grumpy Group
 */

public class ClientDisplayer extends UnicastRemoteObject
		implements ClientDisplayerInterface, Serializable {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -586751614177645291L;
	/**
	 * The {@link ClientInterface client} used 
	 */
	private ClientImplementation client;
	private DiscussionSubjectInterface currentDiscussion = null;
	private ClientMainFrame mainFrame;

	/**
	 * Instance constructor
	 * @throws RemoteException
	 */
	public ClientDisplayer() throws RemoteException {
		super();
		this.mainFrame=new ClientMainFrame(this);
	}

	@Override
	public DiscussionSubjectInterface getCurrentDiscussion()
			throws RemoteException {
		return currentDiscussion;
	}

	@Override
	public void setCurrentDiscussion(DiscussionSubjectInterface currentDiscussion)
			 throws RemoteException {
		this.currentDiscussion = currentDiscussion;
	}

	@Override
	public ClientImplementation getClient() throws RemoteException {
		return this.client;
	}
	
	@Override
	public void setClient(ClientImplementation client) throws RemoteException {
		this.client = client;
	}

	@Override
	public ClientMainFrame getMainFrame() throws RemoteException {
		return this.mainFrame;
	}
	
	@Override
	public void exit() throws RemoteException {
		//r.exit(this);
		this.display("See you later dude!", true);
		System.exit(0);
	}
	
	@Override
	public void error(String message, boolean inFrame) throws RemoteException {
		if(inFrame) {
			JOptionPane.showMessageDialog(this.mainFrame, "<html>"+
					message.replaceAll("\n", "<br>")+"</html>",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		System.err.println("[ERROR]: "+message);
		this.mainFrame.displayError(message);
	}

	@Override
	public void display(String message, boolean inFrame) throws RemoteException {
		if(inFrame) {
			JOptionPane.showMessageDialog(this.mainFrame, "<html>"+
					message.replaceAll("\n", "<br>")+"</html>", "Information",
					JOptionPane.INFORMATION_MESSAGE);
		}
		System.out.println("[LOG]: "+message);
		this.mainFrame.displayLog(message);
	}

	@Override
	public void getMessage(String message) throws RemoteException {
		this.display("[SERVER]: "+message, false);
	}
	
	@Override
	public String toString() {
		return "ClientDisplayer [client=" + client + 
				", currentDiscussion=" + currentDiscussion + "]";
	}

}
