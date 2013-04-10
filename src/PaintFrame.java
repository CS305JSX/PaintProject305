import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class PaintFrame extends JFrame {
	
	Socket clientSocket;
	DataOutputStream out;
	DataInputStream in;
	
	
	public PaintFrame(String host, int port){
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		try{
			clientSocket = new Socket(host, port);  
	        out = new DataOutputStream(clientSocket.getOutputStream());
	        in = new DataInputStream(clientSocket.getInputStream());
		}
		catch(IOException ioe){
			System.out.println("IOException setting up applet in frame...");
		}
		
		SidePanel panel = new SidePanel(in, out);
		getContentPane().add(panel);
		PaintApplet app = new PaintApplet(in, out);
		app.setSidePanel(panel);
		getContentPane().add(app);
		setVisible(true);
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public static void main(String[] args){
		if(args.length != 2)printUsageAndQuit();
		
		String host = args[0];
		int port = Integer.parseInt(args[1]); 
		
		new PaintFrame(host, port);
	}
	
	private static void printUsageAndQuit()
	{
		System.out.println("Usage: PaintFrame <host> <port>");
		System.exit(1);
	}
	
}
