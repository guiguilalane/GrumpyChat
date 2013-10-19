package client.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import server.objects.interfaces.DiscussionSubjectInterface;
import client.interfaces.ClientDisplayerInterface;

/**
 * A {@link JPanel} to store all discussions inside
 * @author Grumpy Group
 */
public class DiscussionSubjectMenu extends JPanel implements ActionListener {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 6560966161410462245L;
	private int nbChannels=0;
	private JLabel label=new JLabel(this.nbChannels+" Available subjects:");
	private final JButton createChannel=new JButton("Create a channel");
	private JButton previous=new JButton((char)9668+"");
	private JButton next=new JButton((char)9658+"");
	private JPanel subjectsPanel=new JPanel();
	private JScrollPane subjectsScroll;
	private int scrollWidth=350;
	/**
	 * It sets if the channel panel is correctly positioned
	 */
	private boolean positioned=false;

	/**
	 * Construct a new instance of a SubjectMenu
	 */
	public DiscussionSubjectMenu() {
		super();
		this.setMinimumSize(new Dimension(this.scrollWidth+300,50));
		this.subjectsPanel.setBorder(null);
		this.subjectsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.subjectsScroll=new JScrollPane(this.subjectsPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.subjectsScroll.setPreferredSize(new Dimension(this.scrollWidth,25));
		this.subjectsScroll.setBorder(null);
		this.subjectsScroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
		
		this.createChannel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.label.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(this.createChannel);
		this.add(this.label);
		this.add(this.previous);
		this.add(this.subjectsScroll);
		this.add(this.next);

		this.previous.addActionListener(this);
		this.next.addActionListener(this);
	}
	
	public void reset() {
		this.subjectsPanel.removeAll();
		this.nbChannels=0;
		this.label.setText(this.nbChannels+" Available subjects:");
	}

	public void updatePanel() {
		if(this.positioned) {
			return;
		}
		Point currentPos=this.subjectsScroll.getViewport().getViewPosition();
		this.subjectsScroll.getViewport().setViewPosition(new Point(currentPos.x,currentPos.y+6));
		this.subjectsPanel.repaint();
		this.positioned=true;
		
	}
	
	public void setScrollWidth(int width) {
		this.scrollWidth=width-370;
		this.subjectsPanel.setMinimumSize(new Dimension(this.scrollWidth+300,50));
		this.subjectsScroll.setPreferredSize(new Dimension(this.scrollWidth,25));
		this.revalidate();
	}

	/**
	 * Add a new discussion subject to the current menu.
	 * @param subjectName
	 */
	public void addDiscussionSubject(final ClientDisplayerInterface client,
			final DiscussionSubjectInterface subject) {
		this.nbChannels++;
		this.label.setText(this.nbChannels+" Available subjects:");
		JButton dsButton = new JButton();
		dsButton.setLayout(new FlowLayout());
		dsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					boolean connected=subject.isConnected(client.getClient());
					if(connected||client.getServer().subscribe(subject, client)) {
						if(client.isOpenedDiscussion(subject)) {
							if(!client.getDiscussionFrame(subject).isVisible()) {
								client.getDiscussionFrame(subject).setVisible(true);
							}
						}
						else {
							client.openDiscussion(new ClientDiscussionFrame(client, subject));	
						}
					}
					else {
						client.error("You did not success to subscribe to the cannel", true);
					}
				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		try {
			dsButton.setText(subject.getTitle());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		this.subjectsPanel.add(dsButton);
	}
	
	public void removeDiscussionSubject(final ClientDisplayerInterface client,
			final DiscussionSubjectInterface subject) {
		for(Component c:this.subjectsPanel.getComponents()) {
			if(c instanceof JButton) {
				try {
					if(((JButton) c).getText().equalsIgnoreCase(subject.getTitle())) {
						this.subjectsPanel.remove(c);
						this.nbChannels--;
						this.label.setText(this.nbChannels+" Available subjects:");
					}
				} catch (RemoteException e) {
					System.err.println("Can not remove the channel button");
					e.printStackTrace();
					return;
				}
			}
		}
	}

	public void addNullListInformation() {
		this.subjectsPanel.add(new JLabel("No subjects"));
	}

	/**
	 * @return the createChannel
	 */
	public JButton getCreateChannel() {
		return this.createChannel;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(this.previous)) {
			Point currentPos=this.subjectsScroll.getViewport().getViewPosition();
			int newPos=currentPos.x-100;
			newPos=newPos<=0?0:newPos;
			this.subjectsScroll.getViewport().setViewPosition(new Point(newPos,currentPos.y));
			this.subjectsPanel.repaint();
		}
		else if(event.getSource().equals(this.next)) {
			Point currentPos=this.subjectsScroll.getViewport().getViewPosition();
			this.subjectsScroll.getViewport().setViewPosition(new Point(currentPos.x+100,currentPos.y));
			this.subjectsPanel.repaint();
		}
	}

	public boolean isPositioned() {
		return this.positioned;
	}
	
}
