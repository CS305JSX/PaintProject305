package edu.cs305.paintproject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import edu.cs305.paintproject.client.P2PListenerThread;
import edu.cs305.paintproject.client.PaintFrame;
import edu.cs305.paintproject.util.Logger;

public class P2PSendMethods implements MessageSendMethods {
	
	Socket centralServerSocket;
	ObjectOutputStream centralServerOut;
	ArrayList<ObjectOutputStream> outputs;
	PaintFrame frame;
	
	public P2PSendMethods(Socket centralServerSocket, PaintFrame frame){
		this.centralServerSocket = centralServerSocket;
		this.frame = frame;
		try{
			centralServerOut = new ObjectOutputStream(centralServerSocket.getOutputStream());
			centralServerOut.flush();
		}
		catch(IOException ioe){
			Logger.log(ioe, "IOException setting up ouput stream to central server.");
		}
		
		outputs = new ArrayList<ObjectOutputStream>();
	}
	
	public void sendLogout() {
		
	}
	
	public synchronized void sendExitToLobby() {
		for(ObjectOutputStream out: outputs){
			try{
				out.writeObject(Constants.EXIT_TO_LOBBY);
				out.flush();
				out.close();
			}
			catch(IOException ioe){
				Logger.log(ioe, "IOException sending ExitToLobby.");
			}
		}
	}
	
	public synchronized void sendLineSegment(LineSegment line) {
		for(ObjectOutputStream out: outputs){
			try{
				Date date= new Date();
				line.timestamp = new Timestamp(date.getTime());
				out.writeObject(line);
				out.flush();
			}
			catch(IOException ioe){
				Logger.log(ioe, "IOException sending LineSegment to " + out);
			}
		}
	}
	
	public void sendRequestPictureNames() {
		try{
			centralServerOut.writeObject(Constants.REQUEST_PICTURE_NAMES);
			centralServerOut.flush();
		}
		catch(IOException ioe){
			Logger.log(ioe, "IOException requesting picture names.");
		}
	}
	
	public void sendRequestPicture(PictureRequest request) {
		try{
			centralServerOut.writeObject(request);
			centralServerOut.flush();
		}
		catch(IOException ioe){
			Logger.log(ioe, "IOException requesting picture names.");
		}
	}
	
	public synchronized void addDestination(Socket socket) {
		try{
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			outputs.add(out);
		}
		catch(IOException ioe){
			Logger.log(ioe, "IOException adding destination.");
		}
	}
	
	public synchronized void removeDestination(Socket socket) {
		
	}

}
