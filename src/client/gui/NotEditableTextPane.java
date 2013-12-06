package client.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;

public class NotEditableTextPane extends JTextPane {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -1047237860151007086L;
	
	public NotEditableTextPane() {
		super();
		this.setEditable(false);
		this.setAutoscrolls(true);
		this.setBackground(new Color(255,255,255,0));
		this.setOpaque(false);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		this.repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		int width=this.getBounds().width;
		int height=this.getBounds().height;
		int size=Math.min(height, width)-30;
		g.setColor(Color.decode("#FFFFFF"));
		g.fillRect(0, 0, width, height);
		int x=(int) (width-size-50);
		int y=height/2-size/2;
		ImageIcon image=new ImageIcon("img/background.png");
		g.drawImage(image.getImage(),x,y,size,size,this);
        super.paintComponent(g);
	}

}
