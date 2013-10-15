package client.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

import client.interfaces.ClientDisplayerInterface;

import server.objects.interfaces.DiscussionSubjectInterface;

public class DiscussionSubjectMenu extends JPanel {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 6560966161410462245L;
	private final JLabel label=new JLabel("Available subjects:");
	private final JButton createChannel=new JButton("Create a channel");

	/**
	 * Construct a new instance of a SubjectMenu
	 */
	public DiscussionSubjectMenu() {
		super();
	}
	
	public void reset() {
		this.removeAll();
		this.add(this.createChannel);
		this.add(this.label);
	}

	/**
	 * Add a new discussion subject to the current menu.
	 * @param subjectName
	 */
	public void addDiscussionSubject(final ClientDisplayerInterface client,
			final DiscussionSubjectInterface subject) {
		JButton dsButton = new JButton();
		dsButton.setLayout(new FlowLayout());
		dsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				new ClientDiscussionFrame(client, subject);
			}
		});
		try {
			dsButton.setText(subject.getTitle());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		this.add(dsButton);
	}

	public void addNullListInformation() {
		this.add(new JLabel("No subjects"));
	}

	/**
	 * @return the createChannel
	 */
	public JButton getCreateChannel() {
		return createChannel;
	}
	
}
