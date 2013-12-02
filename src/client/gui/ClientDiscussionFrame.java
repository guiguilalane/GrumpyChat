package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import server.objects.Message;
import server.objects.interfaces.DiscussionSubjectInterface;
import server.objects.interfaces.MessageInterface;
import server.objects.interfaces.ServerForumInterface;
import client.exception.DiscussionClosedException;
import client.exception.DiscussionException;
import client.exception.DiscussionNeedOwnerException;
import client.exception.ExceptionManager;
import client.interfaces.ClientDisplayerInterface;
import client.interfaces.ClientInterface;

/**
 * The frame for a specific {@link DiscussionSubjectInterface discussion}
 * 
 * @author Grumpy Group
 */
public class ClientDiscussionFrame extends JFrame implements ActionListener,
		KeyListener {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -4983290848512030124L;

	private DiscussionSubjectInterface discussion;
	private ClientDisplayerInterface client;
	private List<DiscussionException> exceptions = new ArrayList<DiscussionException>();
	private ExceptionManager exceptionManager;
	private boolean onLine = true;

	private JLabel label = new JLabel("Infos");
	private JLabel ownerInfos = new JLabel("Owner");
	private JTextPane log = new JTextPane();
	private StyledDocument style = this.log.getStyledDocument();
	private JScrollPane logScroll;
	private JScrollPane messageScroll;
	private JPanel topPanel = new JPanel();
	private JTextArea messageContent = new JTextArea();
	private JButton removeButton = new JButton("Remove channel");
	private JButton sendButton = new JButton("Send");
	private JButton resetButton = new JButton("Reset");
	private JButton copyButton = new JButton("Copy");
	private JButton clearButton = new JButton("Clear");
	private JButton quitButton = new JButton("Quit");
	private Style errorStyle = this.log.addStyle("ErrorStyle", null);
	private Style infoStyle = this.log.addStyle("InfoStyle", null);
	private Style normalStyle = this.log.addStyle("NormalStyle", null);
	private Style myName = this.log.addStyle("MyNameStyle", null);
	private Style serverStyle = this.log.addStyle("ServerStyle", null);
	private Style otherNames = this.log.addStyle("OtherNamesStyle", null);
	private Map<ClientInterface, Style> allStyles = new HashMap<ClientInterface, Style>();

	private int COLOR_INDEX = 0;

	public ClientDiscussionFrame(ClientDisplayerInterface client,
			DiscussionSubjectInterface discussion) throws HeadlessException {

		super("Discussion: ");

		final int frameWidth = 600;
		final int frameHeight = 525;

		String title = "error";
		Integer users = 0;
		String owner = "SERVER";
		this.discussion = discussion;
		this.client = client;
		try {
			title = discussion.getTitle();
			users = discussion.getClients().size() - 1;
			owner = this.client.getServer().getOwner(this.discussion);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

		this.setTitle("Discussion: " + title);

		StyleConstants.setForeground(this.errorStyle, Color.red);
		StyleConstants.setForeground(this.infoStyle, Color.decode("#808080"));
		StyleConstants.setItalic(this.infoStyle, true);
		StyleConstants
				.setForeground(this.normalStyle, this.log.getForeground());
		StyleConstants.setForeground(this.myName, Color.decode("#0582E4"));
		StyleConstants.setBold(this.myName, true);
		StyleConstants.setForeground(this.otherNames, Color.decode("#10845A"));
		StyleConstants.setBold(this.otherNames, true);
		StyleConstants.setForeground(this.serverStyle, Color.decode("#100000"));
		StyleConstants.setBold(this.serverStyle, true);
		this.setSize(frameWidth, frameHeight);
		this.setPreferredSize(new Dimension(frameWidth, frameHeight));
		this.setMinimumSize(new Dimension(frameWidth, frameHeight));
		this.setResizable(false);
		this.setLayout(new FlowLayout());
		this.setLocationRelativeTo(null);

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		final ClientDiscussionFrame cdf = this;
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cdf.setVisible(false);
				super.windowClosing(e);
				// if(cdf.askQuit()) {
				// cdf.close();
				// }
			}

			@Override
			public void windowClosed(WindowEvent e) {
				cdf.dispose();
				super.windowClosed(e);
			}
		});

		this.topPanel.setPreferredSize(new Dimension(frameWidth - 20, 35));
		this.topPanel.setMaximumSize(new Dimension(frameWidth - 20, 35));
		this.topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.label.setText("Discussion " + title + ": " + users + " users");
		this.label.setPreferredSize(new Dimension(frameWidth - 240, 15));
		this.label.setMaximumSize(new Dimension(frameWidth - 240, 15));
		this.ownerInfos.setText("Owner: " + owner);
		this.topPanel.add(this.label);
		this.removeButton.addActionListener(this);
		this.removeButton.setToolTipText("Remove this channel");
		try {
			if (this.client.getServer().isChannelOwner(this.client, title)) {
				this.topPanel.add(this.removeButton);
			} else {
				this.topPanel.add(this.ownerInfos);
			}
		} catch (RemoteException e) {
			System.err.println("Can not add the remove button");
		}

		this.log.setEditable(false);
		this.log.setAutoscrolls(true);
		this.log.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		this.logScroll = new JScrollPane(this.log);
		this.logScroll.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		this.logScroll.setPreferredSize(new Dimension(frameWidth - 10, 350));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setPreferredSize(new Dimension(frameWidth - 10, 35));
		buttonPanel.add(this.sendButton);
		buttonPanel.add(this.resetButton);
		buttonPanel.add(this.copyButton);
		buttonPanel.add(this.clearButton);
		buttonPanel.add(this.quitButton);
		this.sendButton.addActionListener(this);
		this.sendButton.setToolTipText("Send your message");
		this.resetButton.addActionListener(this);
		this.resetButton.setToolTipText("Clear your current message");
		this.clearButton.addActionListener(this);
		this.clearButton.setToolTipText("Clear the current discussion");
		this.copyButton.addActionListener(this);
		this.copyButton.setToolTipText("Copy the current discussion");
		this.quitButton.addActionListener(this);
		this.quitButton.setToolTipText("Leave the channel");

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setPreferredSize(new Dimension(frameWidth - 10, 90));

		this.messageContent.setAutoscrolls(true);
		this.messageContent.addKeyListener(this);
		this.messageScroll = new JScrollPane(this.messageContent);
		this.messageScroll.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		this.messageScroll.setPreferredSize(new Dimension(frameWidth - 10, 45));
		bottomPanel.add(this.messageScroll);
		bottomPanel.add(buttonPanel);

		this.getContentPane().add(this.topPanel, BorderLayout.NORTH);
		this.getContentPane().add(this.logScroll, BorderLayout.CENTER);
		this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		this.setVisible(true);

		try {
			if (discussion.getMessages() != null
					&& !discussion.getMessages().isEmpty()) {
				for (MessageInterface msg : discussion.getMessages()) {
					this.displayMessage(msg);
				}
			}
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

		this.exceptionManager = new ExceptionManager(this);
		this.exceptionManager.start();

		this.info("You are now connected to '" + title + "' channel");
	}

	@Override
	public void repaint() {
		try {
			super.repaint();
		} catch (Exception e) {
			System.err.println("Error while repainting discussion frame");
		}
	}

	public DiscussionSubjectInterface getDiscussion() {
		return this.discussion;
	}

	public ClientDisplayerInterface getClient() {
		return this.client;
	}

	protected boolean askQuit() {
		return JOptionPane.showConfirmDialog(this, "Do you want to leave?",
				"Quit? :(", JOptionPane.YES_NO_OPTION) == 0;
	}

	protected boolean askRemove() throws HeadlessException, RemoteException {
		return JOptionPane.showConfirmDialog(
				this,
				"Do you want really remove the channel "
						+ this.discussion.getTitle() + "?", "Remove? :O",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == 0;
	}

	/**
	 * Close the frame after inform the client of this closing
	 */
	public void closeFrame() {
		synchronized (this.exceptions) {
			this.exceptions.add(new DiscussionClosedException());
		}
	}

	/**
	 * Ask for a new discussion owner
	 */
	public void askNewOwner() {
		synchronized (this.exceptions) {
			this.exceptions.add(new DiscussionNeedOwnerException());
		}
	}

	public void print(String message) {
		this.write(message, this.normalStyle);
	}

	public void info(String info) {
		this.write(info, this.infoStyle);
	}

	public void error(String error) {
		this.write(error, this.errorStyle);
	}

	private Style newStyle(ClientInterface client) {
		Style s = null;
		try {
			synchronized (this.log) {
				s = this.log.addStyle(client.getPseudo() + "Style", null);
			}
			StyleConstants.setForeground(s,
					ClientDiscussionFrame.COLORS[this.COLOR_INDEX++
							% ClientDiscussionFrame.COLORS.length]);
			StyleConstants.setBold(s, true);
		} catch (RemoteException e) {
			e.printStackTrace();
			return this.otherNames;
		}
		return s;
	}

	public void newUser(ClientInterface client) {
		String title = "error";
		Integer users = 0;
		try {
			title = this.discussion.getTitle();
			users = this.discussion.getClients().size() - 1;
			this.info(client.getPseudo() + " is now connected on the channel");
			synchronized (this.allStyles) {
				this.allStyles.put(client, this.newStyle(client));
			}
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		this.label.setText("Discussion " + title + ": " + users + " users");
		this.repaint();
	}

	public void leftUser(ClientInterface client) {
		String title = "error";
		Integer users = 0;
		try {
			title = this.discussion.getTitle();
			users = this.discussion.getClients().size() - 1;
			this.info(client.getPseudo() + " has left the channel");
			synchronized (this.allStyles) {
				this.allStyles.put(client, this.newStyle(client));
			}
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		this.label.setText("Discussion " + title + ": " + users + " users");
		this.repaint();
	}

	public void changeOwner() {
		try {
			String owner = this.client.getServer().getOwner(this.discussion);
			if (this.client.getClient().getPseudo().equalsIgnoreCase(owner)) {
				this.topPanel.removeAll();
				this.topPanel.add(this.label);
				this.topPanel.add(this.removeButton);
				this.topPanel.revalidate();
			} else {
				this.ownerInfos.setText("Owner: " + owner);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		this.onLine = true;
		this.repaint();
	}

	public synchronized void displayMessage(MessageInterface message) {
		try {
			boolean me = message.getClient().equals(this.client.getClient());
			boolean server = message.getClient().equals(
					ServerForumInterface.CLIENT);
			StyleConstants.setItalic(this.infoStyle, false);
			this.style.insertString(this.style.getLength(),
					message.getDateString(), this.infoStyle);
			StyleConstants.setItalic(this.infoStyle, true);
			Style s = this.myName;
			if (server) {
				s = this.serverStyle;
			} else if (!me) {
				synchronized (this.allStyles) {
					if (!this.allStyles.containsKey(message.getClient())) {
						s = this.newStyle(message.getClient());
						this.allStyles.put(message.getClient(), s);
					} else {
						s = this.allStyles.get(message.getClient());
					}
				}
			}
			this.style.insertString(this.style.getLength(), me ? "Me" : message
					.getClient().getPseudo(), s);
			if (!server) {
				this.style.insertString(this.style.getLength(),
						": " + message.getMessage() + "\n", this.normalStyle);
			} else {
				StyleConstants.setItalic(this.infoStyle, false);
				this.style.insertString(this.style.getLength(),
						": " + message.getMessage() + "\n", this.infoStyle);
				StyleConstants.setItalic(this.infoStyle, true);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		this.log.setCaretPosition(this.style.getLength());
	}

	public synchronized void write(String message, Style style) {
		try {
			this.style.insertString(this.style.getLength(), message + "\n",
					style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		this.log.setCaretPosition(this.style.getLength());
	}

	public void setInactive() {
		this.onLine = false;
	}

	public void close() throws InterruptedException {
		try {
			if (this.discussion.isConnected(this.client)) {
				this.client.getServer().unsubscribe(this.discussion,
						this.client);
			}
			this.client.closeDiscussion(this.discussion);
			this.exceptionManager.interrupt();
			this.dispose();
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this, "Connection seems to be lost",
					"Connection error! x)", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void sendMessage() {
		String message = this.messageContent.getText();
		while (message.startsWith(" ") || message.startsWith("\n")) {
			message = message.substring(1, message.length());
		}
		while (message.endsWith(" ") || message.endsWith("\n")) {
			message = message.substring(0, message.length() - 1);
		}
		if (message.isEmpty()) {
			return;
		}
		try {
			MessageInterface msg = new Message(this.client.getClient(), message);
			boolean added = false;
			if (this.isRemote()) {
				added = this.discussion.addMessage(msg);
				List<ClientDisplayerInterface> clients = this.discussion
						.getClients();
				for (ClientDisplayerInterface cdi : clients) {
					if (this.client == null
							|| !this.client.getClient().equals(cdi.getClient())) {
						cdi.getMessage(msg, this.discussion);
					}
				}
			} else {
				added = this.client.getServer().addMessage(this.client,
						this.discussion, msg);
			}
			if (added) {
				this.client.getMessage(msg, this.discussion);
				this.messageContent.setText("");
			}
		} catch (RemoteException e) {
			try {
				this.client.error(
						"The message can't be sent (Connection error?)", true);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}

	public List<DiscussionException> getExceptions() {
		synchronized (this.exceptions) {
			return this.exceptions;
		}
	}

	public void tryToBeNewOwner() {
		boolean newOwner = false;
		try {
			newOwner = this.client.getServer().setNewOwner(this.discussion,
					this.client);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (newOwner) {
			try {
				this.client.display("You are now the new owner of discussion '"
						+ this.discussion.getTitle() + "'", this);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			try {
				this.client.display("Someone else has been fatser", this);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getSource().equals(this.messageContent)) {
			if (event.getKeyCode() == KeyEvent.VK_ENTER) {
				if (event.isControlDown()) {
					this.actionPerformed(new ActionEvent(this.sendButton, event
							.getID(), event.getKeyChar() + ""));
					synchronized (this.messageContent) {
						this.messageContent.setText("");
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (event.getSource().equals(this.messageContent)) {
			if (event.getKeyCode() == KeyEvent.VK_ENTER) {
				//
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent event) {
		if (event.getSource().equals(this.messageContent)) {
			if (event.getKeyCode() == KeyEvent.VK_ENTER) {
				//
			}
		}
	}

	private boolean isRemote() {
		try {
			return this.discussion.getUrl() != null;
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (!this.onLine
				&& (event.getSource().equals(this.quitButton) || event
						.getSource().equals(this.sendButton))) {
			JOptionPane.showMessageDialog(this,
					"The discussion is not available at the moment. "
							+ "Please try again later", "Discussion busy! :@",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (event.getSource().equals(this.quitButton)) {
			if (this.askQuit()) {
				try {
					if (this.discussion.getOwner() != null
							&& this.discussion.getOwner().getClient()
									.equals(this.client.getClient())) {
						if (this.isRemote()) {
							List<ClientDisplayerInterface> clients = this.discussion
									.getClients();
							synchronized (clients) {
								for (ClientDisplayerInterface cdi : clients) {
									if (cdi.isOpenedDiscussion(this.discussion)) {
										cdi.setInactiveDiscussion(this.discussion);
									}
								}
							}
							synchronized (clients) {
								for (ClientDisplayerInterface cdi : clients) {
									if (cdi.isOpenedDiscussion(this.discussion)) {
										cdi.askNewOwner(this.discussion);
									}
								}
							}
							synchronized (clients) {
								this.discussion.setOwner(null);
								for (ClientDisplayerInterface c : clients) {
									if (this.discussion.isConnected(c) && c.isOpenedDiscussion(this.discussion)) {
										c.changeOwner(this.discussion);
									}
								}
							}
						}
						else {
							this.client.serverAskNewOwner(this.discussion);
						}
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				try {
					this.close();
				} catch (InterruptedException e) {
					// Dispose error
				}
			}
		} else if (event.getSource().equals(this.copyButton)) {
			synchronized (this.log) {
				StringSelection selection = new StringSelection(
						this.log.getText());
				Clipboard clipboard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				clipboard.setContents(selection, null);
			}
		} else if (event.getSource().equals(this.clearButton)) {
			synchronized (this.log) {
				this.log.setText("");
			}
		} else if (event.getSource().equals(this.resetButton)) {
			synchronized (this.messageContent) {
				this.messageContent.setText("");
			}
		} else if (event.getSource().equals(this.sendButton)) {
			synchronized (this.messageContent) {
				this.sendMessage();
			}
		} else if (event.getSource().equals(this.removeButton)) {
			try {
				if (this.askRemove()) {
					if (!this.client.getServer().isChannelOwner(this.client,
							this.discussion.getTitle())) {
						this.client
								.error("You are not the channel owner", true);
						return;
					}
					DiscussionSubjectInterface dsi = this.client.getServer()
							.remove(this.client, this.discussion.getTitle());
					if (dsi != null) {
						this.client.updateChannelList(this.client.getServer()
								.getDiscussions());
						this.client.display(
								"The channel '" + this.discussion.getTitle()
										+ "' has been correctly removed", true);
						try {
							this.close();
						} catch (InterruptedException e) {
							// Dispose error
						}
						this.client.getServer().closeFrames(this.client,
								this.discussion);
					} else {
						this.client.error(
								"The channel '" + this.discussion.getTitle()
										+ "' can not be remove", true);
					}
				}
			} catch (HeadlessException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.client == null) ? 0 : this.client.hashCode());
		result = prime * result
				+ ((this.discussion == null) ? 0 : this.discussion.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ClientDiscussionFrame)) {
			return false;
		}
		try {
			return ((ClientDiscussionFrame) obj).getDiscussion().getTitle()
					.equalsIgnoreCase(this.getDiscussion().getTitle())
					&& ((ClientDiscussionFrame) obj).getClient().getClient()
							.equals(this.getClient().getClient());
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		} catch (ClassCastException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String toString() {
		return "ClientDiscussionFrame [discussion=" + this.discussion
				+ ", client=" + this.client + "]";
	}

	private static final Color[] COLORS = { Color.decode("#9932CC"),
			Color.decode("#006400"), Color.decode("#8A2BE2"),
			Color.decode("#A52A2A"), Color.decode("#5F9EA0"),
			Color.decode("#D2691E"), Color.decode("#6495ED"),
			Color.decode("#DC143C"), Color.decode("#00008B"),
			Color.decode("#008B8B"), Color.decode("#B8860B"),
			Color.decode("#B2B2B2"), Color.decode("#0000FF"),
			Color.decode("#8B008B"), Color.decode("#556B2F"),
			Color.decode("#FF8C00"), Color.decode("#FF7F50"),
			Color.decode("#8B0000"), Color.decode("#483D8B"),
			Color.decode("#2F4F4F"), Color.decode("#00CED1"),
			Color.decode("#9400D3"), Color.decode("#FF1493"),
			Color.decode("#00BFFF"), Color.decode("#696969"),
			Color.decode("#1E90FF"), Color.decode("#B22222"),
			Color.decode("#228B22"), Color.decode("#FF00FF"),
			Color.decode("#DAA520"), Color.decode("#808080"),
			Color.decode("#008000"), Color.decode("#ADFF2F"),
			Color.decode("#FF69B4"), Color.decode("#CD5C5C"),
			Color.decode("#4B0082"), Color.decode("#F08080"),
			Color.decode("#20B2AA"), Color.decode("#778899"),
			Color.decode("#00FF00"), Color.decode("#32CD32"),
			Color.decode("#FF00FF"), Color.decode("#800000"),
			Color.decode("#66CDAA"), Color.decode("#0000CD"),
			Color.decode("#BA55D3"), Color.decode("#9370DB"),
			Color.decode("#3CB371"), Color.decode("#7B68EE"),
			Color.decode("#48D1CC"), Color.decode("#C71585"),
			Color.decode("#191970"), Color.decode("#000080"),
			Color.decode("#808000"), Color.decode("#6B8E23"),
			Color.decode("#FFA500"), Color.decode("#FF4500"),
			Color.decode("#DA70D6"), Color.decode("#DB7093"),
			Color.decode("#CD853F"), Color.decode("#800080"),
			Color.decode("#FF0000"), Color.decode("#BC8F8F"),
			Color.decode("#4169E1"), Color.decode("#8B4513"),
			Color.decode("#FA8072"), Color.decode("#FAA460"),
			Color.decode("#2E8B57"), Color.decode("#A0522D"),
			Color.decode("#87CEEB"), Color.decode("#6A5ACD"),
			Color.decode("#708090"), Color.decode("#00FF7F"),
			Color.decode("#4682B4"), Color.decode("#008080"),
			Color.decode("#EE82EE"), Color.decode("#9ACD32") };

}
