package edu.cs305.paintproject.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.cs305.paintproject.Constants;
import edu.cs305.paintproject.LineSegment;
import edu.cs305.paintproject.PictureRequest;
import edu.cs305.paintproject.util.Logger;

public class P2PListener {
	
	ServerSocket serverSocket;
	/*private*/ ArrayList<String> connections;
	ArrayList<ListenerThread> listenerThreads;
	ArrayList<LineSegment> segments;
	PaintFrame frame;
	public long timeDifference;
	
	ListenerThread centralServerListener;
	
	
	public P2PListener(PaintFrame frame){
		this.frame = frame;
		timeDifference = 0;
		
		connections = new ArrayList<String>();
		segments = new ArrayList<LineSegment>();
		listenerThreads = new ArrayList<ListenerThread>();
		
		try{
			serverSocket = new ServerSocket(7777);
		}
		catch(IOException ioe){
			Logger.log("Unable to create server.");
		}
		
		centralServerListener = new ListenerThread(frame.centralServerSocket, frame, this);
		centralServerListener.start();
	}
	
	public synchronized void addLineSegment(LineSegment segment, PaintApplet applet){
		if(segments.size() == 0){
			segments.add(segment);
			applet.giveCommand(segment);
			return;
		}
		
		int index = segments.size()-1;
		for(int i=segments.size()-1; i>=0; i--){
			LineSegment current = segments.get(i);
			
			if(segment.time > current.time){
				segments.add(i+1, segment);
				index = i+1;
				break;
			}
		}
		
		for(int i=index; i<segments.size(); i++){
			applet.giveCommand(segments.get(i));
		}
		
	}
	
	public synchronized boolean containsConnectionTo(String address){
		for(String a: connections){
			if(a.equals(address)){
				Logger.log("TRUE");
				return true;
			}
		}
		Logger.log("FALSE");
		return false;
	}
	
	public synchronized void addConnection(String address){
		connections.add(address);
	}
	
	public synchronized void removeConnection(String address){
		connections.remove(address);
	}
	
	public synchronized void addListenerThread(ListenerThread lt){
		listenerThreads.add(lt);
	}
	
	public synchronized void removeListenerThread(ListenerThread lt){
		listenerThreads.remove(lt);
	}
	
	public synchronized void reconnectToCentralServer(){
		for(ListenerThread lt: listenerThreads){
			lt.interrupt();
			if(lt.in != null){
				try{
					lt.in.close();
				}
				catch(IOException ioe){
					Logger.log(ioe, "IOException closing ListenerThread's input stream.");
				}
			}
			if(lt.socket != null){
				try{
					lt.socket.close();
				}
				catch(IOException ioe){
					Logger.log(ioe, "IOException closing ListenerThread's socket.");
				}
			}
		}
		
		connections.clear();
		listenerThreads.clear();
		segments.clear();
		
		centralServerListener.notifyReconnectToCentralServer();
	}
}

class ListenerThread extends Thread {
	
	Socket socket;
	PaintFrame frame;
	P2PListener listener;
	ObjectInputStream in;
	
	public ListenerThread(Socket socket, PaintFrame frame, P2PListener listener){
		Logger.log("new listener");
		this.socket = socket;
		this.frame = frame;
		this.listener = listener;
		
		
		if(socket != null){
			try{
				in = new ObjectInputStream(socket.getInputStream());
			}
			catch(IOException ioe){
				Logger.log(ioe, "IOException creating input stream.");
			}
		}
		else{
			listener.addListenerThread(this);
		}
	}
	
	private synchronized void waitForCentralServer(){
		try {
			wait();
		} catch (InterruptedException ie) {
			Logger.log(ie, "Interrupted Exception while waiting.");
		}
	}
	
	public synchronized void notifyReconnectToCentralServer(){
		try{
			frame.centralServerSocket = new Socket(frame.host, frame.port);
			in = new ObjectInputStream(frame.centralServerSocket.getInputStream());
		}
		catch(IOException ioe){
			Logger.log(ioe, "IOException reconnecting to CentralServer");
		}
		
		notify();
	}
	
	boolean receivedImage = false;
	
	@SuppressWarnings("unchecked")
	public void run(){
		while(socket == null){
			try{
				socket = listener.serverSocket.accept();
				Logger.log("1: accepted connection " + socket.getInetAddress().getHostAddress());
				String address = socket.getInetAddress().getHostAddress();
				if(!listener.containsConnectionTo(address)){
					listener.addConnection(address);
					frame.msm.addDestination(new Socket(socket.getInetAddress(), 7777));
					Logger.log("1: outputting to " + address);
				}
				in = new ObjectInputStream(socket.getInputStream());
				Logger.log("1: receiving from " + address);
				
				ListenerThread lt = new ListenerThread(null, frame, listener);
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
				else if(data instanceof PictureRequest){
					Logger.log("Received PictureRequest");
					
					String fileName = frame.applet.getFileName();
					ImageIcon image = new ImageIcon(frame.applet.image);
					
					frame.msm.sendPicture(image, socket.getInetAddress().getHostAddress());
				}
				else if(data instanceof ImageIcon){
					Logger.log("Recieved ImageIcon");
					ImageIcon image = (ImageIcon)data;
					frame.displayApplet(image);
					receivedImage = true;
				}
				else if(data instanceof ArrayList){//list of peers, received after choosing file on server
					Logger.log("Recieved ArrayList");
					ArrayList<String> addresses = (ArrayList<String>)data;
					Logger.log(addresses);
					
					for(String address: addresses){
						Socket socket = new Socket(address, 7777);//hard coded port for now
						listener.addConnection(address);
						frame.msm.addDestination(socket);
						
						Logger.log("AL: outputting to " + address);
					}
					
					ListenerThread lt = new ListenerThread(null, frame, listener);
					lt.start();
					
					in.close();
					frame.msm.closeCentralServerOutput();
					socket.close(); //close connection to the central server
					
					if(!receivedImage)
						frame.msm.sendPictureRequestToPeer();
					waitForCentralServer();
					Logger.log("awoken");
				}
				else if(data instanceof Integer){
					int message = (Integer)data;
					
					if(message == Constants.EXIT_TO_LOBBY){
						listener.removeConnection(socket.getInetAddress().getHostAddress());
						listener.removeListenerThread(this);
						in.close();
						frame.msm.removeDestination(socket);
						socket.close();
						break;
					}
				}
				else if(data instanceof Long){
					long serverTime = (Long)data;
					long localTime = (new Date()).getTime();
					
					listener.timeDifference = serverTime - localTime;
				}
				else if(data instanceof LineSegment){
					
					listener.addLineSegment((LineSegment)data, frame.applet);
				}
			}
			catch(IOException ioe){
				Logger.log(ioe, "IOException while listening.");
				break;
			}
			catch(ClassNotFoundException cnfe){
				Logger.log(cnfe, "ClassNotFoundException while listening.");
			}
		}
	}
	
}
