
import java.io.*;
import java.util.*;
import javax.swing.ImageIcon;
public class ClientListenerThread extends Thread {
	
	ObjectInputStream in;
	PaintFrame frame;
	
	public ClientListenerThread(ObjectInputStream in, PaintFrame frame){
		this.in = in;
		this.frame = frame;
	}
	
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
		catch(IOException ioe){
			System.out.println("IOException in ClientListenerThread...");
		}
		catch(ClassNotFoundException cnfe){
			System.out.println("ClassNotFoundException in ClientListenerThread...");
		}
	}
	
}
