package edu.cs305.paintproject.p2pserver;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import edu.cs305.paintproject.Constants;
import edu.cs305.paintproject.PictureRequest;
import edu.cs305.paintproject.util.Logger;


public class P2PServer {
	
	ArrayList<WorkerThread> lobby = new ArrayList<WorkerThread>();
	HashMap<String, ArrayList<String>> sessions = new HashMap<String, ArrayList<String>>(); //mapping from filename to IP address
	
	public static void main(String[] argv) throws Exception {
		if(argv.length != 1)printUsageAndQuit();
	  		int port = Integer.parseInt(argv[0]);
	  	
	  	Logger.startLogging("server.log");
		new P2PServer().runServer(port);
	}
	
	public void runServer(int port) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(port);
		
		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
			Logger.log(connectionSocket.getInetAddress().getHostAddress());
			removeIfPresent(connectionSocket.getInetAddress().getHostAddress());
			WorkerThread worker = new WorkerThread(connectionSocket, this);
			worker.start();
			addToLobby(worker);
		} 
	}
	
	public synchronized void removeIfPresent(String address){
		for(String string: sessions.keySet()){
			if(sessions.get(string).contains(address)){
				sessions.get(string).remove(address);
				Logger.log("removed");
			}
		}
	}
	
	public synchronized void addToSession(String address, String fileName, WorkerThread worker, boolean updateOthers){
		if(!sessions.containsKey(fileName)){
			ArrayList<String> addresses = new ArrayList<String>();
			sessions.put(fileName, addresses);
			lobby.remove(worker);
		}
		
		Logger.log(sessions);
		
		worker.send(sessions.get(fileName));
		sessions.get(fileName).add(address);
		
		if(updateOthers){
			for(WorkerThread w: lobby){
				w.send(getFiles(new File("Server/Pictures")));
			}
		}
	}
	
	public synchronized void addToLobby(WorkerThread worker){
		lobby.add(worker);
	}
	
	private static void printUsageAndQuit()
	{
		System.out.println("Usage: PaintServer <port>");
		System.exit(1);
	}
	
	public static Vector<String> getFiles(File folder){
		folder.mkdirs();
		Vector<String> fileNames = new Vector<String>();
		for(File file: folder.listFiles()){
			if(!file.isDirectory())
				fileNames.add(file.getName());
		}
		Logger.log(fileNames.toString());
		return fileNames;
	}
	
}

class WorkerThread extends Thread {
	
	Socket peerSocket;
	ObjectInputStream in;
	ObjectOutputStream out;
	P2PServer server;
	boolean available;
	
	public WorkerThread(Socket peerSocket, P2PServer server){
		try{
			this.peerSocket = peerSocket;
			out = new ObjectOutputStream(peerSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(peerSocket.getInputStream());
		}
		catch(IOException ioe){
			Logger.log("Error setting up streams.");
		}
		
		this.peerSocket = peerSocket;
		this.server = server;
		available = true;
	}
	
	public void send(Object data){
		try{
			if(available){
				out.writeObject(data);
				out.flush();
			}
			
			if(data instanceof ArrayList){
				out.close();
				available = false;
			}
		}
		catch(IOException ioe){
			Logger.log("Error sending data to client.");
		}
	}
	
	public void run(){
		Logger.log("Created Worker Thread.");
		
		while(true){
			try{
				Object data = in.readObject();
				
				if(data instanceof PictureRequest){
					boolean updateOthers = false;
					Logger.log("Picutre Requested");
					
					String fileName = ((PictureRequest)data).pictureName;
					File imgFile = new File("Server/Pictures/" + fileName);
					ImageIcon image = null;
					if(!imgFile.exists()){
						updateOthers = true;
						ImageIO.write(new BufferedImage(400,400, BufferedImage.TYPE_3BYTE_BGR), "png", imgFile);
					}
					image = new ImageIcon(ImageIO.read(imgFile));
					
					//if(sessions.get(fileName))
						send(image);
					
					send((new Date()).getTime());
					server.addToSession(peerSocket.getInetAddress().getHostAddress(), fileName, this, updateOthers);
					break;
				}
				else if(data instanceof Integer){
					int message = (Integer)data;
					
					if(message == Constants.REQUEST_PICTURE_NAMES)
						send(P2PServer.getFiles(new File("Server/Pictures")));
				}
			}
			catch(IOException ioe){
				Logger.log(ioe, "IOException getting file name from client.");
			}
			catch(ClassNotFoundException cnfe){
				Logger.log(cnfe, "ClassNotFoundException getting file name from client.");
			}
		}
	}
	
}
