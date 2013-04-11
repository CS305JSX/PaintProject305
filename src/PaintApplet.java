
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.applet.*;
import java.awt.image.*;

public class PaintApplet extends JPanel implements MouseListener, MouseMotionListener {
	
	int prevMouseX = -1;
	int prevMouseY = -1;
	
	BufferedImage image;
	Graphics graphics;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	
	public PaintApplet(ObjectInputStream in, ObjectOutputStream out) {
		setOpaque(false);
		System.out.println("paint");
		addMouseListener(this);
		addMouseMotionListener(this);
        
        setBackground(Color.blue);
        setVisible(true);
        
        image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
		graphics.setColor(Color.black);
		
		this.in = in;
		this.out = out;
	}
	
	public void displayImage(ImageIcon i){
		if(i != null)
			graphics.drawImage(i.getImage(), 0, 0, null);
		else
			image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
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
		int curMouseX = m.getX();
		int curMouseY = m.getY();
		
		 if(prevMouseX != -1 && prevMouseY != -1){
			 graphics.drawLine(prevMouseX, prevMouseY, curMouseX, curMouseY);
			 try{
				 /*byte[] bytes = new byte[32];
				 
				 bytes[0] = MODE_PENCIL;
				 
				 addInt(bytes, prevMouseX, 1);
				 addInt(bytes, prevMouseY, 5);
				 addInt(bytes, curMouseX, 9);
				 addInt(bytes, curMouseY, 13);
				 
				 System.out.print("Send: ");
				 for(int i=0; i<17; i++){
						System.out.print(Long.toHexString(bytes[i] & 0xFF) + " ");
					}
					System.out.println();
				 
				 out.write(bytes);*/
				 LineSegment line = new LineSegment(prevMouseX, prevMouseY, curMouseX, curMouseY);
				 out.writeObject(line);
				 out.flush();
				 out.reset();
			 }
			 catch(IOException ioe){
				 System.out.println("IOException");
			 }
		 }
		
		prevMouseX = m.getX();
		prevMouseY = m.getY();
		repaint();
	}

	
	public void mouseMoved(MouseEvent m) {
		 
		
	}

	
	public void mouseClicked(MouseEvent m) {
		 
		
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
