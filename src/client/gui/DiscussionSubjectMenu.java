package client.gui;

import java.rmi.RemoteException;

import javax.swing.JPanel;
import javax.swing.JButton;

import server.objects.interfaces.DiscussionSubjectInterface;

public class DiscussionSubjectMenu extends JPanel{

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 6560966161410462245L;

	/**
	 * Construct a new instance of a SubjectMenu
	 */
	public DiscussionSubjectMenu(){
		super();
	}

	/**
	 * Add a new discussion subject to the current menu.
	 * @param subjectName
	 */
	public void addDiscussionSubject(DiscussionSubjectInterface subject){
		JButton dsButton = new JButton();

		try {
			dsButton.setText(subject.getTitle());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		this.add(dsButton);
	}
}
