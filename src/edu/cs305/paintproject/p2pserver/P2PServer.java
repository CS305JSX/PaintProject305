package edu.cs305.paintproject.p2pserver;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

import edu.cs305.paintproject.util.Logger;


public class P2PServer {
	
	HashMap<String, InetAddress> sessions = new HashMap<String, InetAddress>();
	
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
		} 
	}
	
	public synchronized void addToSession(InetAddress address, ObjectOutputStream out, String filename){
		if(!sessions.containsKey(filename)){
			sessions.put(filename, address);
		}
		
		try{
			out.writeObject(sessions.get(filename));
		}
		catch(IOException ioe){
			System.out.println("IOException sending main peer to client.");
		}
	}
	
	private static void printUsageAndQuit()
	{
		System.out.println("Usage: PaintServer <port>");
		System.exit(1);
	}
	
}

class FileListenerThread extends Thread {
	
	Socket peerSocket;
	ObjectInputStream in;
	ObjectOutputStream out;
	P2PServer server;
	
	public FileListenerThread(Socket peerSocket, P2PServer server){
		try{
			ObjectOutputStream  out = new ObjectOutputStream(peerSocket.getOutputStream());
			out.flush();
			ObjectInputStream in = new ObjectInputStream(peerSocket.getInputStream());
		}
		catch(IOException ioe){
			System.out.println();
		}
		
		this.peerSocket = peerSocket;
		this.server = server;
	}
	
	public void run(){
		try{
			out.writeObject(getFiles(new File("Server/Pictures")));
			String filename = (String)in.readObject();
			server.addToSession(peerSocket.getInetAddress(), out, filename);
		}
		catch(IOException ioe){
			System.out.println("Error getting file name from client.");
		}
		catch(ClassNotFoundException cnfe){
			System.out.println("Error getting file name from client.");
		}
	}
	
	public Vector<String> getFiles(File folder){
		folder.mkdirs();
		Vector<String> fileNames = new Vector<String>();
		for(File file: folder.listFiles()){
			if(!file.isDirectory())
				fileNames.add(file.getName());
		}
		return fileNames;
	}
}
