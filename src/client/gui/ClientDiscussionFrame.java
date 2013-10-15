package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

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

import server.objects.interfaces.DiscussionSubjectInterface;
import client.interfaces.ClientDisplayerInterface;

public class ClientDiscussionFrame extends JFrame implements ActionListener {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -4983290848512030124L;

	private DiscussionSubjectInterface discussion;
	private ClientDisplayerInterface client;
	
	private JTextPane log=new JTextPane();
	private StyledDocument style=this.log.getStyledDocument();
	private JScrollPane logScroll;
	private JScrollPane messageScroll;
	private JTextArea messageContent=new JTextArea();
	private JButton removeButton=new JButton("Remove channel");
	private JButton sendButton=new JButton("Send");
	private JButton resetButton=new JButton("Reset");
	private JButton copyButton=new JButton("Copy");
	private JButton clearButton=new JButton("Clear");
	private JButton quitButton=new JButton("Quit");
    private Style errorStyle = this.log.addStyle("ErrorStyle", null);
    private Style infoStyle = this.log.addStyle("InfoStyle", null);
    private Style normalStyle = this.log.addStyle("NormalStyle", null);

	public ClientDiscussionFrame(ClientDisplayerInterface client,
			DiscussionSubjectInterface discussion)
			throws HeadlessException {
		
		super("Discussion: ");

		final int frameWidth=600;
		final int frameHeight=500;
		
		JLabel label;
		String title="error";
		Integer users=0;
		try {
			title=discussion.getTitle();
			users=discussion.getClients().size();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		this.discussion = discussion;
		this.client = client;

		this.setTitle("Discussion: "+title);
		label = new JLabel("Discussion "+title+": "+users+" users");
		
	    StyleConstants.setForeground(this.errorStyle, Color.red);
	    StyleConstants.setForeground(this.infoStyle, Color.decode("#808080"));
	    StyleConstants.setForeground(this.normalStyle, this.log.getForeground());
		this.setSize(frameWidth,frameHeight);
		this.setPreferredSize(new Dimension(frameWidth,frameHeight));
		this.setMinimumSize(new Dimension(frameWidth,frameHeight));
		this.setResizable(false);
		this.setLayout(new FlowLayout());
		this.setLocationRelativeTo(null);
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		final ClientDiscussionFrame cdf=this;
		this.addWindowListener(new WindowAdapter() 
			{
				public void windowClosing(WindowEvent e) {
					if(cdf.askQuit()) {
						cdf.close();
					}
				}
			});

		label.setPreferredSize(new Dimension(frameWidth-40,15));
		label.setMaximumSize(new Dimension(frameWidth-40,15));

		this.log.setEditable(false);
		this.log.setAutoscrolls(true);
		this.log.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		this.logScroll = new JScrollPane(this.log);
		this.logScroll.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		this.logScroll.setPreferredSize(new Dimension(frameWidth-10,350));

		JPanel buttonPanel=new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setPreferredSize(new Dimension(frameWidth-10,35));
		buttonPanel.add(this.removeButton);
		buttonPanel.add(this.sendButton);
		buttonPanel.add(this.resetButton);
		buttonPanel.add(this.copyButton);
		buttonPanel.add(this.clearButton);
		buttonPanel.add(this.quitButton);
		this.removeButton.addActionListener(this);
		this.sendButton.addActionListener(this);
		this.resetButton.addActionListener(this);
		this.clearButton.addActionListener(this);
		this.copyButton.addActionListener(this);
		this.quitButton.addActionListener(this);
		
		JPanel bottomPanel=new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setPreferredSize(new Dimension(frameWidth-10,90));

		this.messageContent.setAutoscrolls(true);
		this.messageScroll = new JScrollPane(this.messageContent);
		this.messageScroll.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		this.messageScroll.setPreferredSize(new Dimension(frameWidth-10,45));
		bottomPanel.add(this.messageScroll);
		bottomPanel.add(buttonPanel);
		
		this.getContentPane().add(label,BorderLayout.NORTH);
		this.getContentPane().add(this.logScroll,BorderLayout.CENTER);
		this.getContentPane().add(bottomPanel,BorderLayout.SOUTH);
		this.setVisible(true);
		
		this.info("You are now connected to '"+title+"' channel");
	}
	
	protected boolean askQuit() {
		return JOptionPane.showConfirmDialog(this,
				"Do you want to leave?", "Quit? :(",
				JOptionPane.YES_NO_OPTION)==0;
	}
	
	protected boolean askRemove() throws HeadlessException, RemoteException {
		return JOptionPane.showConfirmDialog(this,
				"Do you want really remove the channel "+this.discussion.getTitle()+
				"?", "Remove? :O",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE)==0;
	}

	public void close() {
//		this.client.unsubscribe(this.discussion);
		this.dispose();
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
	
	public void write(String message, Style style) {
		try {
			this.style.insertString(this.style.getLength(), message+"\n", style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		this.log.setCaretPosition(this.style.getLength());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(this.quitButton)) {
			if(this.askQuit()) {
				this.close();
			}
		}
		if(event.getSource().equals(this.removeButton)) {
			try {
				if(this.askRemove()) {
					if(!this.client.getServer().isChannelOwner(client, this.discussion.getTitle())) {
						client.error("You are not the channel owner", true);
						return;
					}
					DiscussionSubjectInterface dsi=this.client.getServer().remove(client, this.discussion.getTitle());
					if(dsi!=null) {
						if(client.getCurrentDiscussion().equals(dsi)) {
							client.addDiscussion(null);
						}
						client.display("The channel '"+this.discussion.getTitle()+"' has been correctly " +
								"removed", true);
					}
					else {
						client.error("The channel '"+this.discussion.getTitle()+"' can not be remove", true);
					}
				}
			} catch (HeadlessException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

}
