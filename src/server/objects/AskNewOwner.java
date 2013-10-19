package server.objects;

import java.rmi.RemoteException;

import server.objects.interfaces.DiscussionSubjectInterface;
import server.objects.interfaces.ServerForumInterface;
import client.interfaces.ClientDisplayerInterface;

public class AskNewOwner extends Thread {

	private ClientDisplayerInterface cdi;
	private ServerForumInterface sfi;
	private DiscussionSubjectInterface dsi;
	private boolean changed=false;
	
	public AskNewOwner(ServerForumInterface sfi, ClientDisplayerInterface cdi,
			DiscussionSubjectInterface dsi) {
		super();
		this.sfi=sfi;
		this.dsi=dsi;
		this.cdi=cdi;
	}
	
	@Override
	public void run() {
		super.run();
		try {
			this.cdi.display("Ask!", this.cdi.getDiscussionFrame(this.dsi));
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.interrupt();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public boolean getChanged() {
		return this.changed;
	}
}
