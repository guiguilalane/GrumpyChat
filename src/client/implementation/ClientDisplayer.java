package client.implementation;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

import server.objects.Message;
import server.objects.interfaces.DiscussionSubjectInterface;
import server.objects.interfaces.MessageInterface;
import server.objects.interfaces.ServerForumInterface;
import client.interfaces.ClientDisplayerInterface;



public class ClientDisplayer extends UnicastRemoteObject implements ClientDisplayerInterface, Serializable {
	
	protected ClientDisplayer() throws RemoteException {
		super();
	}

	/**
	 * ID
	 */
	private static final long serialVersionUID = -586751614177645291L;
	private ClientImplementation client;
	private int tries=0;
	private DiscussionSubjectInterface currentDiscussion=null;


	@Override
	public ClientImplementation getClient() throws RemoteException {
		return this.client;
	}
	
	@Override
	public void start() throws RemoteException
	{
		this.display("Client starting...");
		int port=1099;
		String url = null;
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
			url="//"+InetAddress.getLocalHost().getHostName()+":"+port+"/GrumpyChat";
			ServerForumInterface r = (ServerForumInterface) Naming.lookup(url);
			
			this.tries=0;
			
			String command="";
			Scanner sc=new Scanner(System.in);
			
			this.display("Type your pseudo and press [ENTER]: ");
			command=sc.nextLine();
			while(r.containsPseudo(command)||command.equalsIgnoreCase("server")) {
				this.error("Pseudo '"+command+"' already used, please try again: ");
				command=sc.nextLine();
			}
			this.client=new ClientImplementation(command);
			r.newUser(this);
			this.display("Welcome "+command);
			
			String help="Available commands:\n" +
					"\t/help:\t\t\tDisplay commands\n" +
					"\t/create <channel>:\tCreate the channel <channel> on the server\n" +
					"\t/exit:\t\t\tLeave client\n" +
					"\t/list:\t\t\tDisplay channels list\n" +
					"\t/messages:\t\tDisplay the current discussion messages\n" +
					"\t/say <message>:\tAdd the message <message> from you on your current channel\n" +
					"\t/subscribe <channel>:\tSuscribe to channel <channel>\n" +
					"\t/switch <channel>:\tSwitch to channel <channel>";
			
			this.display(help);
			
			do {
				command=sc.nextLine();
				if(command.toLowerCase().startsWith("/help")) {
					this.display(help);
				}
				else if(command.toLowerCase().startsWith("/create")) {
					if(r.isFullChannel(this)) {
						this.error("You can not create more channel, delete one before");
						continue;
					}
					int len="/create".length();
					if(command.length()<="/create".length()+1) {
						this.error("Please specify a message");
						continue;
					}
					String subject=command.substring(len+1,command.length());
					if(subject.endsWith(" ")) {
						subject=subject.substring(0,subject.lastIndexOf(" "));
					}
					if(r.obtientSujet(subject)!=null) {
						this.error("The channel '"+subject+"' already exists");
						continue;
					}
					DiscussionSubjectInterface dsi = r.create(this,subject);
					if(dsi!=null) {
						this.currentDiscussion=dsi;
						this.display("The channel '"+subject+"' has been correctly created");
					}
					else {
						this.error("The channel '"+subject+"' can not be created");
					}
				}
				else if(command.toLowerCase().startsWith("/list")) {
					if(r.getDiscussions().isEmpty()) {
						this.error("There is no discussion on the forum");
					}
					String list="Discussions list:\n";
					for(DiscussionSubjectInterface dsi:r.getDiscussions()) {
						list+="\t\t"+dsi.getTitle()+(dsi.isConnected(this.client)?
								" *":"")+"\n";
					}
					this.display(list);
				}
				else if(command.toLowerCase().startsWith("/messages")) {
					if(this.currentDiscussion==null) {
						this.error("You don't have current discussion");
					}
					else {
						List<MessageInterface> messages=this.currentDiscussion
								.getMessages();
						for(MessageInterface msg:messages) {
							this.display(msg.getDateString()+msg.getClient().getPseudo()+
									": "+msg.getMessage());
						}
					}
				}
				else if(command.toLowerCase().startsWith("/remove")) {
					int len="/remove".length();
					if(command.length()<="/remove".length()+1) {
						this.error("Please specify a message");
						continue;
					}
					String subject=command.substring(len+1,command.length());
					if(subject.endsWith(" ")) {
						subject=subject.substring(0,subject.lastIndexOf(" "));
					}
					if(!r.isChannelOwner(this, subject)) {
						this.error("You are not the channel owner");
						continue;
					}
					DiscussionSubjectInterface dsi=r.remove(this, subject);
					if(dsi!=null) {
						if(this.currentDiscussion.equals(dsi)) {
							this.currentDiscussion=null;
						}
						this.display("The channel '"+subject+"' has been correctly removed");
					}
					else {
						this.error("The channel '"+subject+"' can not be remove");
					}
				}
				else if(command.toLowerCase().startsWith("/say")) {
					int len="/say".length();
					if(command.length()<="/say".length()+1) {
						this.error("Please specify a message");
						continue;
					}
					String message=command.substring(len+1,command.length());
					if(message.endsWith(" ")) {
						message=message.substring(0,message.lastIndexOf(" "));
					}
					if(this.currentDiscussion==null) {
						this.error("You don't have current discussion");
					}
					else {
						this.currentDiscussion.addMessage(
								new Message(this.client, message));
					}
					
				}
				else if(command.toLowerCase().startsWith("/subscribe")) {
					if(command.length()<="/subscribe".length()+1) {
						this.error("Please specify the channel name");
						continue;
					}
					int len="/subscribe".length();
					String subject=command.substring(len+1,command.length());
					if(subject.endsWith(" ")) {
						subject=subject.substring(0,subject.lastIndexOf(" "));
					}
					this.display("Subscribe to: "+subject);
					DiscussionSubjectInterface dsi=r.obtientSujet(subject);
					if(dsi!=null) {
						if(r.subscribe(dsi,this)) {
							this.display("You are now connected to '"+dsi.getTitle()+
									"' channel");
							this.currentDiscussion=dsi;
						}
						else {
							this.error("You failed to connect to '"+dsi.getTitle()+
									"' channel");
						}
					}
					else {
						this.error("The channel '"+subject+"' has not been found");
					}
				}
				else if(command.toLowerCase().startsWith("/switch")) {
					if(command.length()<="/switch".length()+1) {
						this.error("Please specify the channel name");
						continue;
					}
					int len="/switch".length();
					String subject=command.substring(len+1,command.length());
					if(subject.endsWith(" ")) {
						subject=subject.substring(0,subject.lastIndexOf(" "));
					}
					this.display("Switch to: "+subject);
					DiscussionSubjectInterface dsi=r.obtientSujet(subject);
					if(dsi!=null) {
						if(dsi.isConnected(this.client)) {
							this.display("You switched to '"+dsi.getTitle()+"' channel");
							this.currentDiscussion=dsi;
						}
						else {
							this.error("You did not subscribe to '"+dsi.getTitle()+
									"' channel");
						}
					}
					else {
						this.error("The channel '"+subject+"' has not been found");
						command="";
					}
				}
			} while(!command.equalsIgnoreCase("/exit"));
			this.display("Fin du client");
			System.exit(0);
		} catch (ConnectException e) {
			if(this.tries<5) {
				int wait=this.tries*5;
				wait=wait==0?1:wait;
				this.error("Connection error, next try in "+(wait)+" seconds");
				try {
					Thread.sleep(wait*1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				this.tries++;
				this.start();
			}
			else {
				this.error("Can not join the server");
			}
		} catch (MalformedURLException e) {
			this.error("The serveur URL seems to be malformed");
			e.printStackTrace();
			System.exit(0);
		} catch (NotBoundException e) {
			this.error("The serveur bound does not work");
			e.printStackTrace();
			System.exit(0);
		} catch (UnknownHostException e) {
			this.error("The serveur host seems not exist");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	@Override
	public void error(String message) throws RemoteException {
		System.err.println("[ERROR]: "+message);
	}

	@Override
	public void getMessage(String message) throws RemoteException {
		this.display("[SERVER]: "+message);
	}

	@Override
	public void display(String message) throws RemoteException {
		System.out.println("[LOG]: "+message);
	}
	
	@Override
	public String toString() {
		return "ClientDisplayer [client=" + client + 
				", currentDiscussion=" + currentDiscussion + "]";
	}

	public static void main(String[] args) {
		ClientDisplayer cd;
		try {
			cd = new ClientDisplayer();
			cd.start();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
