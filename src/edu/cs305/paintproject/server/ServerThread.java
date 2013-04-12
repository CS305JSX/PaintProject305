package edu.cs305.paintproject.server;
import java.io.*;
import java.util.*;

import edu.cs305.paintproject.util.Logger;

public class ServerThread extends Thread {
	
	boolean available;
	DataInputStream inFromClient;
	private DataOutputStream outToClient;
	ArrayList<ServerThread> clients;

	public ServerThread(DataInputStream inFromClient, DataOutputStream outToClient){
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
		
		clients = new ArrayList<ServerThread>();
		available = true;
	}
	
	public synchronized boolean send(boolean close, byte[] bytes){
		if(close){
			try{
				outToClient.close();
				available = false;
			}
			catch(IOException ioe){
				Logger.log(ioe,"while closing output stream...");
			}
		}
		
		if(!available)
			return false;
		
		try{
			outToClient.write(bytes);
		}
		catch(IOException ioe){
			Logger.log(ioe,"while sending to client...");
		}
		
		return true;
	}
	
	public void run(){
		try{
			while(true){
				byte[] bytes = new byte[32];
				inFromClient.readFully(bytes);
				
				boolean close = false;
				if(bytes[0] == 0)
					close = true;
				
				Logger.log("Rec: ");
				for(int i=0; i<bytes.length; i++){
					Logger.log(bytes[i] + " ");
				}
				Logger.log();
				
				if(!close){
					for(int i=0; i<clients.size(); i++){
						ServerThread thread = clients.get(i);
						
						if(!thread.send(false, bytes))
							clients.set(i, null);
						Logger.log("written");
					}
					
					while(clients.remove(null)){}
				}
				else{
					send(true, bytes);
					break;
				}
			}
		}
		catch(IOException ioe){
			Logger.log(ioe);
		}
		Logger.log("Exiting Thread...");
	}
}
