import java.io.*;  
import java.net.*;  
import java.util.*;

class PaintServer {
	
	
	
	public PaintServer(){
		
	}
	
  public static void main(String argv[]) throws Exception {
	  System.out.println("Starting Server....");
	  
	  ArrayList<ServerThread> workerThreads = new ArrayList<ServerThread>();
	  ArrayList<DataOutputStream> streams = new ArrayList<DataOutputStream>();
	  
      ServerSocket welcomeSocket = new ServerSocket(12356);
      while(true) {  
           Socket connectionSocket = welcomeSocket.accept();
           DataInputStream in = new DataInputStream(connectionSocket.getInputStream());  
           DataOutputStream  out = new DataOutputStream(connectionSocket.getOutputStream());
           ServerThread thread = new ServerThread(in, out);
           
           thread.clients.addAll(workerThreads);
           
           for(ServerThread t: workerThreads){
        	   t.clients.add(thread);
           }
           
           workerThreads.add(thread);
           streams.add(out);
           
           System.out.println("Creating Thread");
           thread.start(); 
        }  
    }  	
}