import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.applet.*;
import java.awt.image.*;

public class PaintApplet extends JPanel implements MouseListener, MouseMotionListener {
	
	public static final byte MODE_LOGOUT = 0;
	public static final byte MODE_PENCIL = 1;
	public static final byte MODE_BUCKET = 2;
	
	int prevMouseX = -1;
	int prevMouseY = -1;
	
	byte id;
	int[] data;
	
	BufferedImage image;
	Graphics graphics;
	
	DataOutputStream out;
	DataInputStream in;
	
	public PaintApplet(DataInputStream in, DataOutputStream out) {
		setOpaque(false);
		System.out.println("paint");
		addMouseListener(this);
		addMouseMotionListener(this);
        
        ClientListenerThread clt = new ClientListenerThread(in, this);
        clt.start();
        
        setBackground(Color.blue);
        setVisible(true);
        
        image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
		graphics = image.createGraphics();
		graphics.setColor(Color.black);
		
		id = 0;
		data = new int[20];
		
		this.in = in;
		this.out = out;
	}
	
	public synchronized void giveCommand(byte id, int[] data){
		this.id = id;
		for(int i=0; i < data.length; i++){
			this.data[i] = data[i];
		}
		
		repaint();
	}
	
	public void paintComponent(Graphics g){
		//System.out.println("paint: " + Long.toHexString(lineStartX) + " " + Long.toHexString(lineStartY) + " " + Long.toHexString(lineEndX) + " " + Long.toHexString(lineEndY));
		if(id == 1){
			graphics.drawLine(data[0], data[1], data[2], data[3]);
		}
		
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
				 byte[] bytes = new byte[32];
				 
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
				 
				 //String string = prevMouseX + " " + prevMouseY + " " + curMouseX + " " + curMouseY + "\n";
				 //System.out.println("Send: " + string);
				 //out.writeUTF(string);
				 out.write(bytes);
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
