package edu.cs305.paintproject.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import edu.cs305.paintproject.Constants;
import edu.cs305.paintproject.util.Logger;


class PaintServer {
	
	HashMap<String, ArrayList<WorkerThread>> sessions = new HashMap<String, ArrayList<WorkerThread>>();
	HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	
	public PaintServer(){
		
	}
	
	public static void main(String[] argv) throws Exception {
		if(argv.length != 1)printUsageAndQuit();
	  	int port = Integer.parseInt(argv[0]);

	  	Logger.startLogging("server.log");
		new PaintServer().runServer(port);
	}
	
	public void runServer(int port) throws Exception {
		Logger.log("Starting Server....");
		
		ArrayList<WorkerThread> lobby = new ArrayList<WorkerThread>();
		sessions.put("lobby", lobby);
		
		ServerSocket welcomeSocket = new ServerSocket(port);
		
		while(true) {  
			Socket connectionSocket = welcomeSocket.accept();
			ObjectOutputStream  out = new ObjectOutputStream(connectionSocket.getOutputStream());
			out.flush();
			ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());
			
			WorkerThread thread = new WorkerThread(in, out, this);
			
			addThreadToSession(thread, lobby);
			
			Logger.log("Creating Thread");
			thread.start();
		} 	 
	}
	
	public synchronized BufferedImage exitLobbyTo(WorkerThread thread, String pictureName){
		ArrayList<WorkerThread> lobby = sessions.get("lobby");
		lobby.remove(thread);
		
		//create new session or add to existing
		thread.sessionMembers.clear();
		ArrayList<WorkerThread> session = sessions.get(pictureName);
		Logger.log(sessions.toString());
		
		//create a new session if one doesn't already exist for the picture
		if(session == null){
			Logger.log("creating session");
			session = new ArrayList<WorkerThread>();
			sessions.put(pictureName, session);
			
			addThreadToSession(thread, session);
			
			BufferedImage image = null;
			try{
				File imgFile = new File("Server/Pictures/" + pictureName);
				if(!imgFile.exists())
				{
						ImageIO.write(new BufferedImage(400,400, BufferedImage.TYPE_3BYTE_BGR), "png", imgFile);
						
						for(String assesion : sessions.keySet())
						{
							for(WorkerThread worker : sessions.get(assesion))
								worker.send(Constants.REFRESH_PICTURE_NAMES, assesion);
						}
				}
				
				image = ImageIO.read(imgFile);
			}
			catch(IOException ioe){
				Logger.log(ioe,"while getting image in exit lobby...");
			}
			images.put(pictureName, image);
		}
		
		return images.get(pictureName);
	}
	
	public synchronized void returnToLobbyFrom(WorkerThread thread, String pictureName, BufferedImage image){
		sessions.get(pictureName).remove(thread);
		if(sessions.get(pictureName).size() == 0){
			sessions.remove(pictureName);
			try{
				ImageIO.write(image, "png", new File("Server/Pictures/" + pictureName));
				Logger.log("Saved image");
			}
			catch(IOException ioe){
				Logger.log(ioe,"while saving image...");
			}
		}
		thread.sessionMembers.clear();
		addThreadToSession(thread, sessions.get("lobby"));
	}
	
	public void addThreadToSession(WorkerThread thread, ArrayList<WorkerThread> session){
		thread.sessionMembers.addAll(session);
		
		for(WorkerThread t: session){
			t.sessionMembers.add(thread);
		}
		
		session.add(thread);
	}
	
	public static String getFiles(File folder){
		String files = "";
		for(File file: folder.listFiles()){
			if(!file.isDirectory())
				files += file.getName() + "\n";
		}
		return files;
	}

	private static void printUsageAndQuit()
	{
		System.out.println("Usage: PaintServer <port>");
		System.exit(1);
	}

}
