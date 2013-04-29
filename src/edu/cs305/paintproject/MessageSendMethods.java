package edu.cs305.paintproject;

public interface MessageSendMethods {
	
	public void sendLogout();
	public void sendExitToLobby();
	public void sendLineSegment(LineSegment line);
	public void sendRequestPictureNames();
	public void sendRequestPicture(PictureRequest request);
}
