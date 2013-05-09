package edu.cs305.paintproject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.swing.ImageIcon;

import edu.cs305.paintproject.client.PaintFrame;
import edu.cs305.paintproject.util.Logger;

public class P2PSendMethods implements MessageSendMethods {
	
	ObjectOutputStream centralServerOut;
	public ArrayList<ObjectOutputStream> outputs;
	public ArrayList<String> addresses;
	PaintFrame frame;
	
	public P2PSendMethods(PaintFrame frame){
		this.frame = frame;
		try{
			centralServerOut = new ObjectOutputStream(frame.centralServerSocket.getOutputStream());
			centralServerOut.flush();
		}
		catch(IOException ioe){
			Logger.log(ioe, "IOException setting up ouput stream to central server.");
		}
		
		outputs = new ArrayList<ObjectOutputStream>();
		addresses = new ArrayList<String>();
	}
	
	public void sendLogout() {
		sendExitToLobby();
		System.exit(0);
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
		outputs.clear();
		addresses.clear();
		
		frame.p2pListener.reconnectToCentralServer();
		try{
			centralServerOut = new ObjectOutputStream(frame.centralServerSocket.getOutputStream());
			centralServerOut.flush();
			
			Logger.log("SENT BACKUP: " + frame.applet.getFileName());
			ImageBackup backup = new ImageBackup(frame.applet.getFileName(), new ImageIcon(frame.applet.image));
			centralServerOut.writeObject(backup);
		}
		catch(IOException ioe){
			Logger.log(ioe, "IOException setting up output stream to central server.");
		}
	}
	
	public synchronized void sendLineSegment(LineSegment line) {
		Logger.log(outputs.size());
		for(ObjectOutputStream out: outputs){
			try{
				Date date= new Date();
				line.time = date.getTime() + frame.p2pListener.timeDifference;
				Logger.log("time: " + line.time);
				out.writeObject(line);
				out.flush();
			}
			catch(IOException ioe){
				Logger.log(ioe, "IOException sending LineSegment to " + out);
			}
		}
	}
	
	public synchronized void sendPictureRequestToPeer(){
		try{
			String fileName = (String)frame.start.list.getSelectedValue();
			frame.applet.setFileName(fileName);
			ObjectOutputStream out = outputs.get((new Random()).nextInt(outputs.size()));
			out.writeObject(new PictureRequest(fileName));
		}
		catch(IOException ioe){
			Logger.log("IOException requesting picture from peer.");
		}
	}
	
	public synchronized void sendPicture(ImageIcon image, String address){
		for(int i=0; i<addresses.size(); i++){
			if(addresses.get(i).equals(address)){
				try{
					outputs.get(i).writeObject(image);
					break;
				}
				catch(IOException ioe){
					Logger.log(ioe, "IOException sending image from peer to peer");
				}
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
			frame.applet.setFileName(request.pictureName);
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
			addresses.add(socket.getInetAddress().getHostAddress());
		}
		catch(IOException ioe){
			Logger.log(ioe, "IOException adding destination.");
		}
	}
	
	public synchronized void removeDestination(Socket socket){
		int index = addresses.indexOf(socket.getInetAddress().getHostAddress());
		Logger.log(index);
		
		if(index >= 0){
			addresses.remove(index);
			outputs.remove(index);
		}
	}
	
	public synchronized void closeCentralServerOutput() {
		try{
			centralServerOut.close();
		}
		catch(IOException ioe){
			Logger.log(ioe, "IOException closing centralServerOut.");
		}
	}

}
