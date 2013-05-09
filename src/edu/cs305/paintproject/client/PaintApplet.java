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
	
	String fileName;
	public BufferedImage image;
	Graphics graphics;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	PaintFrame frame;
	
	public PaintApplet(PaintFrame frame) {
		setOpaque(false);
		Logger.log("paint");
		addMouseListener(this);
		addMouseMotionListener(this);
        
        setBackground(Color.blue);
        setVisible(true);
        
        image = new BufferedImage(400, 400, BufferedImage.TYPE_3BYTE_BGR);
        graphics = image.createGraphics();
		graphics.setColor(Color.black);
		
		this.frame = frame;
	}
	
	public synchronized void setFileName(String fileName){
		this.fileName = fileName;
	}
	
	public synchronized String getFileName(){
		return fileName;
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
		if(frame.side.op != SidePanel.Operation.Pencil)return;
		
		int curMouseX = m.getX();
		int curMouseY = m.getY();
		
		 if(prevMouseX != -1 && prevMouseY != -1){
			 LineSegment line = new LineSegment(prevMouseX, prevMouseY, curMouseX, curMouseY, frame.side.color.getRGB(), (byte)frame.side.sizeSlider.getValue());
			 
			 frame.msm.sendLineSegment(line);
			 
			 if(line.time != -1){
				 frame.p2pListener.addLineSegment(line, this);
			 }
			 else{
				 DrawCommands.drawLineSegmentWithWidth(line, graphics);
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
		
		LineSegment line = new LineSegment(curMouseX, curMouseY, curMouseX, curMouseY, frame.side.color.getRGB(), (byte)frame.side.sizeSlider.getValue());
		
		frame.msm.sendLineSegment(line);
		if(line.time != -1){
			frame.p2pListener.addLineSegment(line, this);
		 }
		else{
			DrawCommands.drawLineSegmentWithWidth(line, graphics);
		}
		


		repaint();
	}
	
	public void mouseReleased(MouseEvent m) {
		 prevMouseX = -1;
		 prevMouseY = -1;
		 
		 repaint();
	}
	
	public void mouseEntered(MouseEvent m) {
		 
		
	}

	
	public void mouseExited(MouseEvent m) {
		 
		
	}

	
	public void mousePressed(MouseEvent m) {
		 
		
	}
	
}
