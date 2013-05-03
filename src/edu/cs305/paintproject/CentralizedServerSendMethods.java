package edu.cs305.paintproject;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

import edu.cs305.paintproject.util.Logger;

public class CentralizedServerSendMethods implements MessageSendMethods {
	
	ObjectOutputStream out;
	Socket clientSocket;
	
	public CentralizedServerSendMethods(Socket clientSocket){
		try{
			this.clientSocket = clientSocket;
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
		}
		catch(IOException ioe){
			Logger.log(ioe, "setting up object output stream...");
		}
	}
	
	public void sendLogout(){
		try{
			out.writeObject(Constants.LOGOUT);
			out.flush();
		}
		catch(IOException ioe){
			System.out.println("IOException while logging out.");
		}
	}
	
	public void sendExitToLobby() {
		try{
			out.writeObject(Constants.EXIT_TO_LOBBY);
			out.flush();
		}
		catch(IOException ioe){
			System.out.println("IOException while exiting to lobby.");
		}
	}
	
	public void sendLineSegment(LineSegment line) {
		try{
			out.writeObject(line);
			out.flush();
		}
		catch(IOException ioe){
			System.out.println("IOException while sending LineSegment.");
		}
	}
	
	public void sendRequestPictureNames() {
		try{
			out.writeObject(Constants.REQUEST_PICTURE_NAMES);
			out.flush();
		}
		catch(IOException ioe){
			System.out.println("IOException while sending picture name list request.");
		}
	}
	
	public void sendRequestPicture(PictureRequest request) {
		try{
			out.writeObject(request);
			out.flush();
		}
		catch(IOException ioe){
			System.out.println("IOException while requesting a picture.");
		}
	}

	@Override
	public void addDestination(Socket socket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeDestination(Socket socket) {
		// TODO Auto-generated method stub
		
	}
	
}
