package edu.cs305.paintproject.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.cs305.paintproject.LineSegment;
import edu.cs305.paintproject.util.Logger;

public class P2PListenerThread extends Thread {
	
	static ServerSocket serverSocket;
	static ArrayList<String> connections;
	static ArrayList<LineSegment> segments;
	
	static{
		connections = new ArrayList<String>();
		segments = new ArrayList<LineSegment>();
		
		try{
			serverSocket = new ServerSocket(7777);
		}
		catch(IOException ioe){
			Logger.log("Unable to create server.");
		}
	}
	
	public static synchronized void addLineSegment(LineSegment segment, PaintApplet applet){
		if(segments.size() == 0){
			segments.add(segment);
			applet.giveCommand(segment);
			return;
		}
		
		int index = segments.size()-1;
		for(int i=segments.size()-1; i>=0; i--){
			LineSegment current = segments.get(i);
			
			if(segment.timestamp.after(current.timestamp)){
				segments.add(i+1, segment);
				index = i+1;
				break;
			}
		}
		
		/*for(LineSegment line: segments){
			System.out.print(line.timestamp + ", ");
		}
		System.out.println();*/
		
		for(int i=index; i<segments.size(); i++){
			//if(index != segments.size()-1)
			//	Logger.log("Out Of Order: " +segments.get(i).timestamp);
			applet.giveCommand(segments.get(i));
			//if(index != segments.size()-1 && i == segments.size()-1)
			//	Logger.log("Done");
		}
		
	}
	
	Socket socket;
	ObjectInputStream in;
	PaintFrame frame;
	
	public P2PListenerThread(Socket socket, PaintFrame frame){
		Logger.log("new listener");
		try{
			if(socket != null){
				in = new ObjectInputStream(socket.getInputStream());
			}
		}
		catch(IOException ioe){
			Logger.log(ioe, "IOException creating input stream.");
		}
		
		this.socket = socket;
		this.frame = frame;
	}
	
	@SuppressWarnings("unchecked")
	public void run(){
		while(socket == null){
			try{
				socket = serverSocket.accept();
				Logger.log("accepted connection " + socket.getInetAddress().toString());
				String address = socket.getInetAddress().toString();
				if(!connections.contains(address)){
					connections.add(address);
					frame.msm.addDestination(new Socket(socket.getInetAddress(), 7777));
				}
				in = new ObjectInputStream(socket.getInputStream());
				
				P2PListenerThread lt = new P2PListenerThread(null, frame);
				lt.start();
			}
			catch(IOException ioe){
				Logger.log(ioe, "IOException accepting connection");
			}
		}
		
		while(true){
			try{
				Object data = in.readObject();
				
				if(data instanceof Vector){
					Logger.log("Recieved Vector");
					frame.start.list.setListData((Vector<String>)data);
				}
				else if(data instanceof ImageIcon){
					Logger.log("Recieved ImageIcon");
					ImageIcon image = (ImageIcon)data;
					frame.displayApplet(image);
					
					P2PListenerThread lt = new P2PListenerThread(null, frame);
					lt.start();
					in.close();
					break;
				}
				else if(data instanceof ArrayList){//list of peers, received after choosing file on server
					Logger.log("Recieved ArrayList");
					ArrayList<String> addresses = (ArrayList<String>)data;
					Logger.log(addresses);
					
					for(String address: addresses){
						Socket socket = new Socket(address, 7777);//hard coded port for now
						connections.add(socket.getInetAddress().toString());
						frame.msm.addDestination(socket);
						
						Logger.log("connected to " + address);
					}
				}
				else if(data instanceof Integer){
					int message = (Integer)data;
					
					
				}
				else if(data instanceof LineSegment){
					P2PListenerThread.addLineSegment((LineSegment)data, frame.applet);
				}
			}
			catch(IOException ioe){
				Logger.log(ioe, "IOException while listening.");
			}
			catch(ClassNotFoundException cnfe){
				Logger.log(cnfe, "ClassNotFoundException while listening.");
			}
		}
	}

}
