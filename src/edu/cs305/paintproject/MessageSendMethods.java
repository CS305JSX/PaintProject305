package edu.cs305.paintproject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.cs305.paintproject.util.Logger;

public interface MessageSendMethods {
	
	public void sendLogout();
	public void sendExitToLobby();
	public void sendLineSegment(LineSegment line);
	public void sendRequestPictureNames();
	public void sendRequestPicture(PictureRequest request);
	
	public void addDestination(Socket socket);
	public void removeDestination(Socket socket);
	public void closeCentralServerOutput();
}
