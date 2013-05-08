package edu.cs305.paintproject.client;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;
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
	
	public Socket centralServerSocket;
	public MessageSendMethods msm;
	
	String host;
	int port;
	
	StartPanel start;
	PaintApplet applet;
	SidePanel side;
	
	public Thread clt;
	public P2PListener p2pListener;
	
	public PaintFrame(String host, int port, int serverType){
		this.host = host;
		this.port = port;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
				
		try{
			centralServerSocket = new Socket(host, port);
		}		
		catch(IOException ioe){
			Logger.log(ioe, "setting up object streams...");
		}
		
		if(serverType == Constants.CENTRALIZED_SERVER){
			msm = new CentralizedServerSendMethods(centralServerSocket);
			clt = new ClientListenerThread(centralServerSocket, this);
			clt.start();
			
			p2pListener = null;
		}
		else if(serverType == Constants.PEER_TO_PEER){
			msm = new P2PSendMethods(this);
			p2pListener = new P2PListener(this);
			
			clt = null;
		}
		
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				Logger.log("close");
				if(clt != null)
					clt.interrupt();
				msm.sendLogout();
				System.exit(0);
			}
		});
		
		side = new SidePanel(this);
		applet = new PaintApplet(this);
		start = new StartPanel(this);
		
		getContentPane().add(start);
		getContentPane().add(side);
		getContentPane().add(applet);
		
		displayStartScreen();
		
		setVisible(true);
		setSize(500, 500);

		try{
			this.setIconImage(ImageIO.read(this.getClass().getResource("img/bucket.png")));
		}catch(Exception e)
		{
			//never mind then.
		}
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
