
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class PaintFrame extends JFrame {
	
	Socket clientSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	StartPanel start;
	PaintApplet applet;
	SidePanel side;
	
	ClientListenerThread clt;
	
	public PaintFrame(){
		setSize(500, 500);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		try{
			clientSocket = new Socket("localhost", 12356);  
	        out = new ObjectOutputStream(clientSocket.getOutputStream());
	        out.flush();
	        in = new ObjectInputStream(clientSocket.getInputStream());
		}		
		catch(IOException ioe){
			System.out.println("IOException setting up object streams...");
		}
		
		clt = new ClientListenerThread(in, this);
		clt.start();
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.out.println("close");
				clt.interrupt();
				try{
					out.writeObject(Constants.LOGOUT);
					out.flush();
					System.exit(0);
				}
				catch(IOException ioe){
					System.out.println("IOException while logging out...");
				}
				
			}
		});
		
		side = new SidePanel(in, out, this);
		applet = new PaintApplet(in, out);
		start = new StartPanel(in, out, this);
		
		getContentPane().add(start);
		getContentPane().add(side);
		getContentPane().add(applet);
		
		setVisible(true);
	}
	
	public void displayApplet(ImageIcon i){
		applet.displayImage(i);
		//getContentPane().remove(start);
		//getContentPane().add(side);
		//getContentPane().add(applet);
		start.setVisible(false);
		side.setVisible(true);
		applet.setVisible(true);
		validate();
	}
	
	public void displayStartScreen(){
		//getContentPane().add(start);
		//getContentPane().remove(side);
		//getContentPane().remove(applet);
		start.setVisible(true);
		side.setVisible(false);
		applet.setVisible(false);
		validate();
	}
	
	//public void windowClosing(WindowEvent e){
		
	//}
		
	public static void main(String[] args){
		new PaintFrame();
	}
	
}
