package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import client.implementation.ClientDisplayer;

/**
 * This class is the main frame of the client side
 * @author Grumpy Group
 */
public class ClientMainFrame extends JFrame {

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
	 * @throws HeadlessException
	 */
	public ClientMainFrame() throws HeadlessException {
		super();
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
		this.setLocationRelativeTo(null);
		
		/*
		 * To define the close operation
		 */
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		final ClientMainFrame cmf = this;
		
		this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					int leave=JOptionPane.showConfirmDialog(cmf,
							"Do you want to leave?", "Quit? :(",
							JOptionPane.YES_NO_OPTION);
					if(leave==0) {
						try {
							cmf.cd.exit();
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
						cmf.dispose();
					}
				}
			});
		
		this.setLayout(new BorderLayout());
		
		JPanel bottomPanel=new JPanel();
		bottomPanel.setLayout(new GridLayout(1, 2));
		this.console.setEditable(true);
		this.console.setLayout(new FlowLayout());
		JScrollPane consoleScroll=new JScrollPane(this.console);
		consoleScroll.setMaximumSize(new Dimension(380,100));
		consoleScroll.setMinimumSize(new Dimension(380,100));
		this.console.setMaximumSize(new Dimension(380,100));
		this.console.setMinimumSize(new Dimension(380,100));
		
		JPanel buttonPanel=new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.setMaximumSize(new Dimension(60, 24));
		buttonPanel.setMinimumSize(new Dimension(60, 24));
		JButton clearButton=new JButton("Clear");
		clearButton.setLayout(new FlowLayout());
		buttonPanel.add(clearButton);
		
		bottomPanel.add(consoleScroll);
		bottomPanel.add(buttonPanel);
		
		this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
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
	 * Display trhe server error log in the client side
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

	
}
