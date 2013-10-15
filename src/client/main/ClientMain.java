package client.main;

import java.awt.BorderLayout;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import server.objects.Message;
import server.objects.interfaces.DiscussionSubjectInterface;
import server.objects.interfaces.MessageInterface;
import server.objects.interfaces.ServerForumInterface;
import client.gui.DiscussionSubjectMenu;
import client.implementation.ClientDisplayer;
import client.implementation.ClientImplementation;
import client.interfaces.ClientDisplayerInterface;

/**
 * This is the client launcher
 * @author Grumpy Group
 */
public class ClientMain {

	/**
	 * Connection tries
	 */
	private int tries=0;

	/**
	 * Discussion subjects
	 */
	private DiscussionSubjectMenu discussionSubjects;

	/**
	 * The panel
	 */
	private JPanel panel;

	/**
	 * The client starting method
	 * @param cd {@link ClientDisplayer} - The client displayer
	 * @throws RemoteException
	 */
	//	@SuppressWarnings("resource")
	public void start(ClientDisplayerInterface cd) throws RemoteException {

		this.launchUI(cd);

		if(cd==null) {
			System.err.println("Client displayer could not be instantiated");
			System.exit(0);
		}
		cd.display("Client starting...", false);
		int port=1100;
		String url = null;
		/*
		 * Fait automatiquement
		 */
		//			if (System.getSecurityManager() == null) {
		//				System.setSecurityManager(new RMISecurityManager());
		//			}
		try {
			/*
			 * On n'utilise pas rmi:
			 */
			url="//"+InetAddress.getLocalHost().getHostName()+":"+port+"/GrumpyChat";
			ServerForumInterface server = (ServerForumInterface) Naming.lookup(url);

			// Find all subjects on server 
			try{
				List<DiscussionSubjectInterface> subjects = server.getDiscussions();
				for(DiscussionSubjectInterface subject : subjects){
					System.out.println("Find subject: " + subject.getTitle());
				}
				
			}catch(Exception e){
				e.printStackTrace();
				System.err.println("Can't find subjects on the server ... ");
			}

			this.tries=0;

			String command="";
			Scanner sc=new Scanner(System.in);

			cd.display("Type your pseudo and press [ENTER]:", false);
			command=sc.nextLine();
			while(server.containsPseudo(command)||command.equalsIgnoreCase("server")||
					command.isEmpty()) {
				if(command.length()<3) {
					cd.error("Your pseudo must have minimum 3 characters", true);
					command="";
				}
				else {
					cd.error("Pseudo '"+command+"' already used, please try again", true);
				}
				cd.display("Type your pseudo and press [ENTER]:", false);
				command=sc.nextLine();
			}
			cd.setClient(new ClientImplementation(command));
			server.newUser(cd);
			cd.display("Welcome "+command, true);
			cd.getMainFrame().setTitle("Client: "+command);

			String help="Available commands:\n" +
					"\t/help:\t\t\tDisplay commands\n" +
					"\t/create <channel>:\tCreate the channel <channel> on the server\n" +
					"\t/exit:\t\t\tLeave client\n" +
					"\t/list:\t\t\tDisplay channels list\n" +
					"\t/messages:\t\tDisplay the current discussion messages\n" +
					"\t/say <message>:\tAdd the message <message> from you on your current channel\n" +
					"\t/subscribe <channel>:\tSuscribe to channel <channel>\n" +
					"\t/switch <channel>:\tSwitch to channel <channel>";

			cd.display(help, true);

			do {
				command=sc.nextLine();
				if(command.toLowerCase().startsWith("/help")) {
					cd.display(help, true);
				}
				else if(command.toLowerCase().startsWith("/create")) {
					if(server.isFullChannel(cd)) {
						cd.error("You can not create more channel, delete one before", true);
						continue;
					}
					int len="/create".length();
					if(command.length()<="/create".length()+1) {
						cd.error("Please specify a message", true);
						continue;
					}
					String subject=command.substring(len+1,command.length());
					if(subject.endsWith(" ")) {
						subject=subject.substring(0,subject.lastIndexOf(" "));
					}
					if(server.obtientSujet(subject)!=null) {
						cd.error("The channel '"+subject+"' already exists", true);
						continue;
					}
					DiscussionSubjectInterface dsi = server.create(cd,subject);
					if(dsi!=null) {
						cd.setCurrentDiscussion(dsi);
						cd.display("The channel '"+subject+"' has been correctly " +
								"created", true);
					}
					else {
						cd.error("The channel '"+subject+"' can not be created",
								true);
					}
				}
				else if(command.toLowerCase().startsWith("/list")) {
					if(server.getDiscussions().isEmpty()) {
						cd.error("There is no discussion on the forum", true);
					}
					String list="Discussions list:\n";
					for(DiscussionSubjectInterface dsi:server.getDiscussions()) {
						list+="\t\t"+dsi.getTitle()+(dsi.isConnected(cd.getClient())?
								" *":"")+"\n";
					}
					cd.display(list, false);
				}
				else if(command.toLowerCase().startsWith("/messages")) {
					if(cd.getCurrentDiscussion()==null) {
						cd.error("You don't have current discussion", true);
					}
					else {
						List<MessageInterface> messages=cd.getCurrentDiscussion()
								.getMessages();
						for(MessageInterface msg:messages) {
							cd.display(msg.getDateString()+msg.getClient().getPseudo()+
									": "+msg.getMessage(), false);
						}
					}
				}
				else if(command.toLowerCase().startsWith("/remove")) {
					int len="/remove".length();
					if(command.length()<="/remove".length()+1) {
						cd.error("Please specify a message", true);
						continue;
					}
					String subject=command.substring(len+1,command.length());
					if(subject.endsWith(" ")) {
						subject=subject.substring(0,subject.lastIndexOf(" "));
					}
					if(!server.isChannelOwner(cd, subject)) {
						cd.error("You are not the channel owner", true);
						continue;
					}
					DiscussionSubjectInterface dsi=server.remove(cd, subject);
					if(dsi!=null) {
						if(cd.getCurrentDiscussion().equals(dsi)) {
							cd.setCurrentDiscussion(null);
						}
						cd.display("The channel '"+subject+"' has been correctly " +
								"removed", true);
					}
					else {
						cd.error("The channel '"+subject+"' can not be remove", true);
					}
				}
				else if(command.toLowerCase().startsWith("/say")) {
					int len="/say".length();
					if(command.length()<="/say".length()+1) {
						cd.error("Please specify a message", true);
						continue;
					}
					String message=command.substring(len+1,command.length());
					if(message.endsWith(" ")) {
						message=message.substring(0,message.lastIndexOf(" "));
					}
					if(cd.getCurrentDiscussion()==null) {
						cd.error("You don't have current discussion", true);
					}
					else {
						cd.getCurrentDiscussion().addMessage(
								new Message(cd.getClient(), message));
					}

				}
				else if(command.toLowerCase().startsWith("/subscribe")) {
					if(command.length()<="/subscribe".length()+1) {
						cd.error("Please specify the channel name", true);
						continue;
					}
					int len="/subscribe".length();
					String subject=command.substring(len+1,command.length());
					if(subject.endsWith(" ")) {
						subject=subject.substring(0,subject.lastIndexOf(" "));
					}
					cd.display("Subscribe to: "+subject, false);
					DiscussionSubjectInterface dsi=server.obtientSujet(subject);
					if(dsi!=null) {
						if(server.subscribe(dsi,cd)) {
							cd.display("You are now connected to '"+dsi.getTitle()+
									"' channel", true);
							cd.setCurrentDiscussion(dsi);
						}
						else {
							cd.error("You failed to connect to '"+dsi.getTitle()+
									"' channel", true);
						}
					}
					else {
						cd.error("The channel '"+subject+"' has not been found", true);
					}
				}
				else if(command.toLowerCase().startsWith("/switch")) {
					if(command.length()<="/switch".length()+1) {
						cd.error("Please specify the channel name", true);
						continue;
					}
					int len="/switch".length();
					String subject=command.substring(len+1,command.length());
					if(subject.endsWith(" ")) {
						subject=subject.substring(0,subject.lastIndexOf(" "));
					}
					cd.display("Switch to: "+subject, false);
					DiscussionSubjectInterface dsi=server.obtientSujet(subject);
					if(dsi!=null) {
						if(dsi.isConnected(cd.getClient())) {
							cd.display("You switched to '"+dsi.getTitle()+
									"' channel", false);
							cd.setCurrentDiscussion(dsi);
						}
						else {
							cd.error("You did not subscribe to '"+dsi.getTitle()+
									"' channel", true);
						}
					}
					else {
						cd.error("The channel '"+subject+"' has not been found",
								true);
						command="";
					}
				}
			} while(!command.equalsIgnoreCase("/exit"));
			cd.exit();
		} catch (ConnectException e) {
			if(this.tries<5) {
				int wait=this.tries*5;
				wait=wait==0?1:wait;
				cd.error("Connection error, next try in "+(wait)+" seconds",
						false);
				try {
					Thread.sleep(wait*1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				this.tries++;
				this.start(cd);
			}
			else {
				cd.error("Can not join the server", true);
				System.exit(0);
			}
		} catch (MalformedURLException e) {
			cd.error("The server URL seems to be malformed", true);
			e.printStackTrace();
			System.exit(0);
		} catch (NotBoundException e) {
			cd.error("The server bound does not work", true);
			e.printStackTrace();
			System.exit(0);
		} catch (UnknownHostException e) {
			cd.error("The server host seems not exist", true);
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void launchUI(ClientDisplayerInterface cd) throws RemoteException {
		//repaint
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		this.discussionSubjects = new DiscussionSubjectMenu();
		cd.getMainFrame().addSubjectPanel(discussionSubjects);
	}

	/**
	 * Main class
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new ClientMain().start(new ClientDisplayer());
		} catch (RemoteException e) {
			System.err.println("Error while loading client");
			e.printStackTrace();
		}
	}

}