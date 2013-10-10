package server.objects;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import server.objects.interfaces.DiscussionSubjectInterface;
import server.objects.interfaces.ServerForumInterface;
import client.implementation.ClientImplementation;
import client.interfaces.ClientInterface;


public class ServerForum extends UnicastRemoteObject implements ServerForumInterface {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -4777930444757298232L;
	private List<DiscussionSubjectInterface> discussionSubjects=new ArrayList<DiscussionSubjectInterface>();
	private ClientInterface client = new ClientImplementation("SERVER");
	
	/**
	 * Protected Constructor
	 * @throws RemoteException
	 */
	public ServerForum() throws RemoteException {
		super();		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean containsPseudo(String pseudo) {
		for(DiscussionSubjectInterface dsi:this.discussionSubjects) {
			try {
				for(ClientInterface ci:dsi.getClients()) {
					if(ci.getPseudo().equalsIgnoreCase(pseudo)) {
						return true;
					}
				}
			} catch (RemoteException e) {
				System.err.println("Error while searching pseudo in discussions");
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public List<DiscussionSubjectInterface> getDiscussions()
			throws RemoteException {
		return this.discussionSubjects;
	}
	
	@Override
	public boolean subscribe(DiscussionSubjectInterface dsi, ClientImplementation client)
			throws RemoteException  {
		boolean subscribed = dsi.subscribe(client);
		if(subscribed) {
			System.out.println("The client '"+client.getPseudo()+
					"' subscribed to channel '"+dsi.getTitle()+"'");
			dsi.addMessage(new Message(this.client, "*** client '"+client.getPseudo()+
					"' is now connected on this channel ***"));
		}
		else {
			System.err.println("The client '"+client.getPseudo()+
					"' failed to subscribe to channel '"+dsi.getTitle()+"'");
		}
		return subscribed;
	}

	@Override
	public DiscussionSubjectInterface obtientSujet(String titre)
			throws RemoteException {
		for(DiscussionSubjectInterface dsi:this.discussionSubjects) {
			if(dsi.getTitle().equalsIgnoreCase(titre)) {
				return dsi;
			}
		}
		return null;
	}

	public void initializedDiscutions()
	{
		try {
			this.discussionSubjects.add(new DiscussionSubject("Caca"));
			this.discussionSubjects.add(new DiscussionSubject("Test"));
			this.discussionSubjects.add(new DiscussionSubject("TrucMuche"));
			this.discussionSubjects.add(new DiscussionSubject("Toto"));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String display() {
		return this.toString();
	}

	@Override
	public String getList() throws RemoteException {
		return Arrays.toString(this.discussionSubjects.toArray());
	}

	@Override
	public String toString() {
		return "ServerForum [discussionSubjects=" +
				Arrays.toString(this.discussionSubjects.toArray()) + "]";
	}
}
