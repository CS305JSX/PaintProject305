import java.io.*;
import java.util.*;

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
				System.out.println("IOException while closing output stream...");
			}
		}
		
		if(!available)
			return false;
		
		try{
			outToClient.write(bytes);
		}
		catch(IOException ioe){
			System.out.println("IOException while sending to client...");
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
				
				System.out.print("Rec: ");
				for(int i=0; i<bytes.length; i++){
					System.out.print(bytes[i] + " ");
				}
				System.out.println();
				
				if(!close){
					for(int i=0; i<clients.size(); i++){
						ServerThread thread = clients.get(i);
						
						if(!thread.send(false, bytes))
							clients.set(i, null);
						System.out.println("written");
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
			System.out.println("IOException in thread...");
		}
		System.out.println("Exiting Thread...");
	}
}
