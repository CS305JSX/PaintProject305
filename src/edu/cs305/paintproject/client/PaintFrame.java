package edu.cs305.paintproject.client;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import edu.cs305.paintproject.Constants;
import edu.cs305.paintproject.util.Logger;

@SuppressWarnings("serial")
public class PaintFrame extends JFrame {
	
	Socket clientSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	StartPanel start;
	PaintApplet applet;
	SidePanel side;
	
	ClientListenerThread clt;
	
	public PaintFrame(String host, int port){
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		try{
			clientSocket = new Socket(host, port);  
	        out = new ObjectOutputStream(clientSocket.getOutputStream());
	        out.flush();
	        in = new ObjectInputStream(clientSocket.getInputStream());
		}		
		catch(IOException ioe){
			Logger.log(ioe, "setting up object streams...");
		}
		
		clt = new ClientListenerThread(in, this);
		clt.start();
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				Logger.log("close");
				clt.interrupt();
				try{
					out.writeObject(Constants.LOGOUT);
					out.flush();
					System.exit(0);
				}
				catch(IOException ioe){
					Logger.log(ioe, "while logging out...");
				}
				
			}
		});
		
		side = new SidePanel(in, out, this);
		applet = new PaintApplet(in, out);
		start = new StartPanel(in, out, this);
		applet.setSidePanel(side);
		
		getContentPane().add(start);
		getContentPane().add(side);
		getContentPane().add(applet);
		
		setVisible(true);
		setSize(500, 500);
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
	private static void enableNimbus()
	{
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
			//nimbus not installed
		}
	}
	
	//public void windowClosing(WindowEvent e){
		
	//}
		
	public static void main(String[] args){
		if(args.length != 2)printUsageAndQuit();
		
		String host = args[0];
		int port = Integer.parseInt(args[1]); 
		
		enableNimbus();
		new PaintFrame(host, port);
	}
	private static void printUsageAndQuit()
	{
		System.out.println("Usage: PaintFrame <host> <port>");
		System.exit(1);
	}
	
}
