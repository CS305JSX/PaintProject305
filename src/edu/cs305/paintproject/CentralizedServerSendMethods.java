package edu.cs305.paintproject;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.ImageIcon;

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
	
	public void addDestination(Socket socket) {
		
	}
	
	public void removeDestination(Socket socket) {
		
	}
	
	public void closeCentralServerOutput() {
		
	}

	@Override
	public void sendPictureRequestToPeer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendPicture(ImageIcon image, String string) {
		// TODO Auto-generated method stub
		
	}
	
}
