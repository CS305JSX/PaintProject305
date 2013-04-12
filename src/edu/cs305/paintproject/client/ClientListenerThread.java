package edu.cs305.paintproject.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.cs305.paintproject.util.Logger;

public class ClientListenerThread extends Thread {
	
	ObjectInputStream in;
	PaintFrame frame;
	
	public ClientListenerThread(ObjectInputStream in, PaintFrame frame){
		this.in = in;
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
