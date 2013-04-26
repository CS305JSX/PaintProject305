package edu.cs305.paintproject;

public interface MessageSendMethods {
	
	public void sendExitToLobby();
	public void sendLineSegment(LineSegment line);
	public void sendRequestPictureNames();
	public void sendRequestPicture(PictureRequest request);
}
