package edu.cs305.paintproject;

import java.net.Socket;

import javax.swing.ImageIcon;

public interface MessageSendMethods {
	
	public void sendLogout();
	public void sendExitToLobby();
	public void sendLineSegment(LineSegment line);
	public void sendRequestPictureNames();
	public void sendRequestPicture(PictureRequest request);
	
	public void addDestination(Socket socket);
	public void removeDestination(Socket socket);
	public void closeCentralServerOutput();
	public void sendPictureRequestToPeer();
	public void sendPicture(ImageIcon image, String string);
	
}
