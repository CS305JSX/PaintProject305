package edu.cs305.paintproject.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.cs305.paintproject.Constants;
import edu.cs305.paintproject.util.Logger;

public class ClientListenerThread extends Thread {
	
	Socket clientSocket;
	ObjectInputStream in;
	PaintFrame frame;
	
	public ClientListenerThread(Socket clientSocket, PaintFrame frame){
		this.clientSocket = clientSocket;
		try{
			this.in = new ObjectInputStream(clientSocket.getInputStream());
		}
		catch(IOException ioe){
			Logger.log(ioe, "setting up object input stream...");
		}
		this.frame = frame;
	}
	
	@SuppressWarnings("unchecked")
	public void run(){
		try{
			while(true){
				Object data = in.readObject();
				
				if(data instanceof ImageIcon){
					ImageIcon image = (ImageIcon)data;
					frame.displayApplet(image);
				}
				else if(data instanceof Vector){
					frame.start.list.setListData((Vector<String>)data);
				}
				else if(data instanceof Integer){
					if((Integer)data == Constants.REFRESH_PICTURE_NAMES){
						Logger.log("got refresh reminder");
						try{
							frame.out.writeObject(Constants.REQUEST_PICTURE_NAMES);
							frame.out.flush();
						}
						catch(IOException ioe){
							Logger.log(ioe,"while requesting picture names from client listener thread...");
						}
					}
				}
				else
					frame.applet.giveCommand(data);
			}
		}
		catch(IOException e){
			Logger.log(e);
		}
		catch(ClassNotFoundException e){
			Logger.log(e);
		}
	}
	
}
