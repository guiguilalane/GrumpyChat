package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GetStringDialog extends JDialog implements ActionListener {

	/**
	 * ID
	 */
	private static final long serialVersionUID = 5452317256991837012L;
	private String value=null;
	private JTextField pseudoField=new JTextField();
	private JButton validateButton=new JButton("OK");
	private JButton cancelButton=new JButton("Cancel");

	/**
	 * A JDialog to get a {@link String} value from a {@link JTextField}
	 * @param owner {@link JFrame} - The parent frame
	 * @param title {@link String} - The frame title
	 * @param message {@link String} - The frame message to display
	 * @param fieldName {@link String} - The field name to display
	 * @param modal {@link Boolean boolean} - Set if the frame is modal or no
	 */
	public GetStringDialog(JFrame owner, String title, String message, String fieldName,
			boolean modal) {
		super(owner,title,modal);
		
		this.setSize(350, 280);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(GetStringDialog.DISPOSE_ON_CLOSE);
		
		JPanel textPanel=new JPanel();
		textPanel.setMinimumSize(new Dimension(350,60));
		JLabel text=new JLabel("<html>"+message.replaceAll("\n", "<br/>")+"</html>",
				SwingConstants.CENTER);
		textPanel.add(text);
		
		// Field
		JPanel entryPanel=new JPanel();
		entryPanel.setPreferredSize(new Dimension(320,45));
		JLabel nomLabel=new JLabel(fieldName+":");
		this.pseudoField.setPreferredSize(new Dimension(150,25));
		entryPanel.add(nomLabel);
		entryPanel.add(this.pseudoField);
		
		// Buttons
		JPanel buttonsPanel=new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.setPreferredSize(new Dimension(200, 45));
		this.validateButton.setMnemonic(KeyEvent.VK_O);
		this.cancelButton.setMnemonic(KeyEvent.VK_C);
		buttonsPanel.add(this.cancelButton);
		buttonsPanel.add(this.validateButton);
		this.validateButton.addActionListener(this);
		this.validateButton.requestFocus();
		this.cancelButton.addActionListener(this);
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 10));

		this.getRootPane().setDefaultButton(this.validateButton);
		
		this.getContentPane().add(textPanel, BorderLayout.NORTH);
		this.getContentPane().add(entryPanel, BorderLayout.CENTER);
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		
		this.pack();
		this.setVisible(true);
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(this.cancelButton)) {
			this.value=null;
			this.dispose();
		}
		else if(event.getSource().equals(this.validateButton)) {
			String textValue=this.pseudoField.getText();

			while(textValue.endsWith(" ")) {
				textValue=textValue.substring(0,textValue.lastIndexOf(" "));
			}
			while(textValue.startsWith(" ")) {
				textValue=textValue.substring(0,textValue.lastIndexOf(" "));
			}
			this.value = textValue;
			this.dispose();
		}
	}

}
