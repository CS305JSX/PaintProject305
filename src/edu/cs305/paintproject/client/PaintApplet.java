package edu.cs305.paintproject.client;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import edu.cs305.paintproject.DrawCommands;
import edu.cs305.paintproject.LineSegment;
import edu.cs305.paintproject.util.Logger;

@SuppressWarnings("serial")
public class PaintApplet extends JPanel implements MouseListener, MouseMotionListener {
	
	int prevMouseX = -1;
	int prevMouseY = -1;
	
	BufferedImage image;
	Graphics graphics;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	SidePanel panel;
	public PaintApplet(ObjectInputStream in, ObjectOutputStream out) {
		setOpaque(false);
		Logger.log("paint");
		addMouseListener(this);
		addMouseMotionListener(this);
        
        setBackground(Color.blue);
        setVisible(true);
        
        image = new BufferedImage(400, 400, BufferedImage.TYPE_3BYTE_BGR);
        graphics = image.createGraphics();
		graphics.setColor(Color.black);
		
		this.in = in;
		this.out = out;
	}
	
	public void setSidePanel(SidePanel panel)
	{
		this.panel = panel;
	}
	
	public void displayImage(ImageIcon i){
		if(i != null){
			graphics.clearRect(0, 0, i.getIconWidth(), i.getIconHeight());
			graphics.drawImage(i.getImage(), 0, 0, null);
		}
		else
			image = new BufferedImage(400, 400, BufferedImage.TYPE_3BYTE_BGR);
	}
	
	public synchronized void giveCommand(Object data){
		DrawCommands.drawCommand(data, graphics);
		repaint();
	}
	
	public void paintComponent(Graphics g){
		g.drawImage(image, 0, 0, null);
	}
	
	public void update(Graphics g){
		paint(g);
	}
	
	public void addInt(byte[] bytes, int input, int start){
		for(int i=start; i<start+4; i++){
			 bytes[i] = (byte)((input >> 8*i) & 0xFF);
		 }
	}
	
	public void mouseDragged(MouseEvent m) {
		if(panel.op != SidePanel.Operation.Pencil)return;
		
		int curMouseX = m.getX();
		int curMouseY = m.getY();
		
		 if(prevMouseX != -1 && prevMouseY != -1){
			 LineSegment line = new LineSegment(prevMouseX, prevMouseY, curMouseX, curMouseY, panel.color.getRGB(), (byte)panel.sizeSlider.getValue());
			 DrawCommands.drawLineSegmentWithWidth(line, graphics);
			 try{
				 /*byte[] bytes = new byte[32];
				 
				 bytes[0] = MODE_PENCIL;
				 
				 addInt(bytes, prevMouseX, 1);
				 addInt(bytes, prevMouseY, 5);
				 addInt(bytes, curMouseX, 9);
				 addInt(bytes, curMouseY, 13);
				 
				 Logger.log("Send: ");
				 for(int i=0; i<17; i++){
						System.out.print(Long.toHexString(bytes[i] & 0xFF) + " ");
					}
					System.out.println();
				 
				 out.write(bytes);*/
				 
				 out.writeObject(line);
				 out.flush();
				 out.reset();
			 }
			 catch(IOException ioe){
				 Logger.log(ioe);
			 }
		 }
		
		prevMouseX = m.getX();
		prevMouseY = m.getY();
		repaint();
	}

	
	public void mouseMoved(MouseEvent m) {
		 
		
	}

	
	public void mouseClicked(MouseEvent m) {
		int curMouseX = m.getX();
                int curMouseY = m.getY(); 
		
		LineSegment line = new LineSegment(curMouseX, curMouseY, curMouseX, curMouseY, panel.color.getRGB(), (byte)panel.sizeSlider.getValue());
		DrawCommands.drawLineSegmentWithWidth(line, graphics);
                         try{
                                 /*byte[] bytes = new byte[32];
                                 
                                 bytes[0] = MODE_PENCIL;
                                 
                                 addInt(bytes, prevMouseX, 1);
                                 addInt(bytes, prevMouseY, 5);
                                 addInt(bytes, curMouseX, 9);
                                 addInt(bytes, curMouseY, 13);
                                 
                                 Logger.log("Send: ");
                                 for(int i=0; i<17; i++){
                                                System.out.print(Long.toHexString(bytes[i] & 0xFF) + " ");
                                        }
                                        System.out.println();
                                 
                                 out.write(bytes);*/
                                 
                                 out.writeObject(line);
                                 out.flush();
                                 out.reset();
                         }
                         catch(IOException ioe){
                                 Logger.log(ioe);
                         }


		repaint();
	}

	
	public void mouseEntered(MouseEvent m) {
		 
		
	}

	
	public void mouseExited(MouseEvent m) {
		 
		
	}

	
	public void mousePressed(MouseEvent m) {
		 
		
	}

	
	public void mouseReleased(MouseEvent m) {
		 prevMouseX = -1;
		 prevMouseY = -1;
		 
		 repaint();
	}
	
	
	
}
