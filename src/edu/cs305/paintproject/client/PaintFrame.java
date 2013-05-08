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

import edu.cs305.paintproject.CentralizedServerSendMethods;
import edu.cs305.paintproject.Constants;
import edu.cs305.paintproject.MessageSendMethods;
import edu.cs305.paintproject.P2PSendMethods;
import edu.cs305.paintproject.util.Logger;

@SuppressWarnings("serial")
public class PaintFrame extends JFrame {
	
	Socket clientSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	public MessageSendMethods msm;
	
	StartPanel start;
	PaintApplet applet;
	SidePanel side;
	
	public Thread clt;
	
	public PaintFrame(String host, int port, int serverType){
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		try{
			clientSocket = new Socket(host, port);
		}		
		catch(IOException ioe){
			Logger.log(ioe, "setting up object streams...");
		}
		
		if(serverType == Constants.CENTRALIZED_SERVER){
			msm = new CentralizedServerSendMethods(clientSocket);
			clt = new ClientListenerThread(clientSocket, this);
		}
		else if(serverType == Constants.PEER_TO_PEER){
			msm = new P2PSendMethods(clientSocket, this);
			clt = new P2PListenerThread(clientSocket, this);
		}
		clt.start();
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				Logger.log("close");
				clt.interrupt();
				msm.sendLogout();
				System.exit(0);
			}
		});
		
		Logger.log("here*");
		side = new SidePanel(in, out, this);
		Logger.log("here---");
		applet = new PaintApplet(this);
		start = new StartPanel(in, out, this);
		
		getContentPane().add(start);
		getContentPane().add(side);
		getContentPane().add(applet);
		
		displayStartScreen();
		
		setVisible(true);
		setSize(500, 500);
	}
	
	public void displayApplet(ImageIcon i){
		applet.displayImage(i);
		
		start.setVisible(false);
		side.setVisible(true);
		applet.setVisible(true);
		validate();
	}
	
	public void displayStartScreen(){
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
	
	public static void main(String[] args){
		if(args.length != 2)printUsageAndQuit();
		
		String host = args[0];
		int port = Integer.parseInt(args[1]); 
		
		enableNimbus();
		Logger.startLogging("client.log");
		
		new PaintFrame(host, port, Constants.PEER_TO_PEER);
	}
	private static void printUsageAndQuit()
	{
		System.out.println("Usage: PaintFrame <host> <port>");
		System.exit(1);
	}
	
}
