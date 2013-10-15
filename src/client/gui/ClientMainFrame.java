package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import server.objects.interfaces.DiscussionSubjectInterface;
import client.implementation.ClientDisplayer;

/**
 * This class is the main frame of the client side
 * @author Grumpy Group
 */
public class ClientMainFrame extends JFrame implements ActionListener {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -8604291092461331957L;
	/**
	 * The frame width
	 */
	private int frameWidth=400;
	/**
	 * The frame height
	 */
	private int frameHeight=300;
	/**
	 * The client displayer instance, it defines the clients attributes
	 * in client side
	 */
	private ClientDisplayer cd;
	/**
	 * The globale log console
	 */
	private JTextPane console=new JTextPane();
	/**
	 * For the globale log console styles
	 */
	private StyledDocument style=this.console.getStyledDocument();
    private Style errorStyle = this.console.addStyle("ErrorStyle", null);
    private Style normalStyle = this.console.addStyle("NormalStyle", null);
    /**
     * The channels list buttons
     */
    private DiscussionSubjectMenu subjectsPanel=new DiscussionSubjectMenu();
	private JButton clearButton=new JButton("Clear");
	private JButton copyButton=new JButton("Copy");
	private JButton quitButton=new JButton("Quit");

	/**
	 * @throws HeadlessException
	 */
	public ClientMainFrame() throws HeadlessException {
		super();
	    StyleConstants.setForeground(this.errorStyle, Color.red);
	    StyleConstants.setForeground(this.normalStyle, this.console.getForeground());
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		
		// Set a minimum size to the app
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		int minWidth = 700;
		int minHeight = 280;
		
		// Center the app
		if(width/2>=minWidth)
		{
			this.frameWidth=width/2;
		}
		if(height/2>=minHeight)
		{
			this.frameHeight=height/2;
		}
		
		this.setSize(this.frameWidth, this.frameHeight);
		this.setMinimumSize(new Dimension(minWidth,minHeight));
		this.setTitle("Client");
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		
		/*
		 * To define the close operation
		 */
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		final ClientMainFrame cmf = this;
		
		this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					if(cmf.askQuit()) {
						cmf.close();
					}
				}
			});
		
		this.console.setEditable(true);
		JScrollPane consoleScroll=new JScrollPane(this.console);
		
		JPanel buttonPanel=new JPanel();
		this.clearButton.addActionListener(this);
		this.copyButton.addActionListener(this);
		this.quitButton.addActionListener(this);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(this.clearButton);
		buttonPanel.add(this.copyButton);
		buttonPanel.add(this.quitButton);
		
		bottomPanel.add(buttonPanel,BorderLayout.EAST);
		
		this.subjectsPanel.getCreateChannel().addActionListener(this);

		this.getContentPane().add(this.subjectsPanel, BorderLayout.NORTH);
		this.getContentPane().add(consoleScroll, BorderLayout.CENTER);
		this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Constructor with the client displayer
	 * @param clientDisplayer {@link ClientDisplayer} - The client displayer
	 */
	public ClientMainFrame(ClientDisplayer clientDisplayer) {
		this();
		this.cd=clientDisplayer;
	}

	private void close() {
		try {
			this.cd.exit();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		this.dispose();
	}

	private boolean askQuit() {
		return JOptionPane.showConfirmDialog(this,
				"Do you want to leave?", "Quit? :(",
				JOptionPane.YES_NO_OPTION)==0;
	}

	/**
	 * Display the server log in the client side
	 * @param message {@link String} - The message to display
	 */
	public void displayLog(String message) {
		try {
			this.style.insertString(this.style.getLength(), message+"\n", this.normalStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		this.console.setCaretPosition(this.style.getLength());
	}

	/**
	 * Display the server error log in the client side
	 * @param message {@link String} - The error to display
	 */
	public void displayError(String message) {
		try {
			this.style.insertString(this.style.getLength(), message+"\n", this.errorStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		this.console.setCaretPosition(this.style.getLength());		
	}
	
	public void updateSubjectPanel(List<DiscussionSubjectInterface> list) {
		this.subjectsPanel.reset();
		if(list==null||list.isEmpty()) {
			this.subjectsPanel.addNullListInformation();
		}
		for(DiscussionSubjectInterface dsi:list) {
			this.subjectsPanel.addDiscussionSubject(this.cd,dsi);
		}
		this.subjectsPanel.repaint();
		this.setVisible(true);
	}

	public void addDiscussion(DiscussionSubjectInterface dsi) {
		this.subjectsPanel.addDiscussionSubject(this.cd,dsi);
		this.subjectsPanel.repaint();
		this.setVisible(true);
	}

	public void removeDiscussion(DiscussionSubjectInterface dsi) {
		this.subjectsPanel.addDiscussionSubject(this.cd,dsi);
		this.subjectsPanel.repaint();
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(this.clearButton)) {
			this.console.setText("");
		}
		else if(event.getSource().equals(this.copyButton)) {
			StringSelection selection=new StringSelection(this.console.getText());
			Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
			clipboard.setContents(selection, null);
		}
		else if(event.getSource().equals(this.quitButton)) {
			if(this.askQuit()) {
				this.close();
			}
		}
		else if(event.getSource().equals(this.subjectsPanel.getCreateChannel())) {
			try {
				if(this.cd.getServer().isFullChannel(this.cd)) {
					this.cd.error("You can not create more channel, delete one before", true);
				}

				String subject="";
				do {
					subject=new GetStringDialog(this.cd.getMainFrame(), "Create a channel",
							"Please type the channel name", "Channel name", true).getValue();
					if(subject==null) {
						return;
					}
					if(this.cd.getServer().getSubjectFromName(subject)!=null) {
						this.cd.error("The channel '"+subject+"' already exists", true);
						subject="";
					}
				} while(subject.isEmpty());
				DiscussionSubjectInterface dsi = this.cd.getServer().create(this.cd,subject);
				if(dsi!=null) {
					this.cd.addDiscussion(dsi);
					this.cd.display("The channel '"+subject+"' has been correctly " +
							"created", true);
				}
				else {
					this.cd.error("The channel '"+subject+"' can not be created",
							true);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
}
