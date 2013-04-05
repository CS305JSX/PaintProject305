import java.io.*;
import java.util.*;

public class ClientListenerThread extends Thread {
	
	DataInputStream in;
	PaintApplet applet;
	
	public ClientListenerThread(DataInputStream in, PaintApplet applet){
		this.in = in;
		this.applet = applet;
	}
	
	public int getInt(byte[] bytes, int start){
		int integer = 0;
		
		for(int i=start; i<start+4; i++){
			integer = integer | ((bytes[i] & 0xFF) << 8*i);
		}
		
		return integer;
	}
	
	public void run(){
		try{
			while(true){
				byte[] bytes = new byte[32];
				in.readFully(bytes);
				
				byte mode = bytes[0];
				
				System.out.print("Rec: ");
				for(int i=0; i<17; i++){
					System.out.print(Long.toHexString(bytes[i] & 0xFF) + " ");
				}
				System.out.println();
				
				switch(mode){
					case PaintApplet.MODE_PENCIL:
						applet.giveCommand(mode, new int[]{getInt(bytes, 1), getInt(bytes, 5), getInt(bytes, 9), getInt(bytes, 13)});
						break;
				}
				
				applet.repaint();
			}
		}
		catch(IOException ioe){
			System.out.println("IOException in ClientListenerThread...");
		}
	}
	
}