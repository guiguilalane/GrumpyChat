package client.implementation;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

import server.objects.Message;
import server.objects.interfaces.DiscussionSubjectInterface;
import server.objects.interfaces.MessageInterface;
import server.objects.interfaces.ServerForumInterface;
import client.interfaces.ClientDisplayerInterface;



public class ClientDisplayer implements ClientDisplayerInterface, Serializable {
	
	/**
	 * ID
	 */
	private static final long serialVersionUID = -586751614177645291L;
	private ClientImplementation client;
	private int tries=0;
	private DiscussionSubjectInterface currentDiscussion=null;

	@Override
	@SuppressWarnings("resource")
	public void start() throws RemoteException
	{
		System.out.println("Lancement du client");
		int port=1090;
		/*
		 * Fait automatiquement
		 */
//		if (System.getSecurityManager() == null) {
//			System.setSecurityManager(new RMISecurityManager());
//		}
		try {
			/*
			 * On n'utilise pas rmi:
			 */
			ServerForumInterface r = (ServerForumInterface) Naming.lookup("//"+InetAddress.getLocalHost().getHostName()+":"+port+"/GrumpyChat");
			System.out.println(r.display());
			
			this.tries=0;
			
			String command="";
			Scanner sc=new Scanner(System.in);
			
			System.out.print("Type your pseudo and press [ENTER]: ");
			command=sc.nextLine();
			while(r.containsPseudo(command)||command.equalsIgnoreCase("server"))
			{
				System.err.print("Pseudo '"+command+"' already used, please try again: ");
				command=sc.nextLine();
			}
			this.client=new ClientImplementation(command);
			System.out.println("Welcome "+command);
			
			String help="Available commands:\n" +
					"\t/help:\t\t\tDisplay commands\n" +
					"\t/exit:\t\t\tLeave client\n" +
					"\t/list:\t\t\tDisplay channels list\n" +
					"\t/messages:\t\tDisplay the current discussion messages\n" +
					"\t/subscribe <channel>:\tSuscribe to channel <channel>\n" +
					"\t/switch <channel>:\tSwitch to channel <channel>\n";
			
			System.out.println(help);
			
			do {
				command=sc.nextLine();
				if(command.toLowerCase().startsWith("/help")) {
					System.out.println(help);
				}
				else if(command.toLowerCase().startsWith("/list")) {
					if(r.getDiscussions().isEmpty())
					{
						System.err.println("There is no discussion on the forum");
						command="";
						continue;
					}
					System.out.println("Discussions list:");
					for(DiscussionSubjectInterface dsi:r.getDiscussions())
					{
						System.out.println("\t"+dsi.getTitle()+(dsi.isConnected(this.client)?" *":""));
					}
				}
				else if(command.toLowerCase().startsWith("/messages")) {
					if(this.currentDiscussion==null) {
						System.err.println("You don't have current discussion");
					}
					else {
						List<MessageInterface> messages=this.currentDiscussion.getMessages();
						System.err.println(this.currentDiscussion.getMessages().size());
						for(MessageInterface msg:messages)
						{
							System.out.println(msg.getDateString()+msg.getClient().getPseudo()+": "+msg.getMessage());
						}
					}
				}
				else if(command.toLowerCase().startsWith("/subscribe")) {
					int len="/subscribe".length();
					String subject=command.substring(len+1,command.length());
					if(subject.contains(" ")) {
						subject=subject.substring(0,subject.indexOf(" "));
					}
					System.out.println("Subscribe to: "+subject);
					DiscussionSubjectInterface dsi=r.obtientSujet(subject);
					if(dsi!=null) {
						if(r.subscribe(dsi,this.client)) {
							System.out.println("You are now connected to '"+dsi.getTitle()+"' channel");
							this.currentDiscussion=dsi;
						}
						else {
							System.err.println("You failed to connect to '"+dsi.getTitle()+"' channel");
						}
					}
					else {
						System.err.println("The channel '"+subject+"' has not been found");
						command="";
					}
				}
				else if(command.toLowerCase().startsWith("/switch")) {
					int len="/switch".length();
					String subject=command.substring(len+1,command.length());
					if(subject.contains(" ")) {
						subject=subject.substring(0,subject.indexOf(" "));
					}
					System.out.println("Switch to: "+subject);
					DiscussionSubjectInterface dsi=r.obtientSujet(subject);
					if(dsi!=null) {
						if(dsi.isConnected(this.client)) {
							System.out.println("You switched to '"+dsi.getTitle()+"' channel");
							this.currentDiscussion=dsi;
						}
						else {
							System.err.println("You did not subscribe to '"+dsi.getTitle()+"' channel");
						}
					}
					else {
						System.err.println("The channel '"+subject+"' has not been found");
						command="";
					}
				}
			} while(!command.equalsIgnoreCase("/exit"));
			System.out.println("Fin du client");
		} catch (ConnectException e) {
			if(this.tries<5)
			{
				int wait=this.tries*5;
				wait=wait==0?1:wait;
				System.err.println("Connection error, next try in "+(wait)+" seconds");
				try {
					Thread.sleep(wait*1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				this.tries++;
				this.start();
			}
			else
			{
				System.err.println("Can not join the server");
				System.exit(0);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void display(String message) throws RemoteException
	{
	}

	public static void main(String[] args) {
		ClientDisplayer cd=new ClientDisplayer();
		try {
			cd.start();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
