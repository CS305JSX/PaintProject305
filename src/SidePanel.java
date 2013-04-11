
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class SidePanel extends JPanel implements ActionListener {
	
	JButton pencilButton;
	JButton logoutButton;
	
	ObjectInputStream in;
	ObjectOutputStream out;
	
	PaintFrame frame;
	
	public SidePanel(ObjectInputStream in, ObjectOutputStream out, PaintFrame frame){
		this.in = in;
		this.out = out;
		this.frame = frame;
		
		setOpaque(false);
		pencilButton = new JButton("P");
		logoutButton = new JButton("X");
		
		pencilButton.addActionListener(this);
		logoutButton.addActionListener(this);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(pencilButton);
		add(Box.createRigidArea(new Dimension(10, 10)));
		add(logoutButton);
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == logoutButton){
			byte[] bytes = new byte[32];
			bytes[0] = 0;
			try{
				out.writeObject(Constants.EXIT_TO_LOBBY);
				frame.displayStartScreen();
			}
			catch(IOException ioe){
				System.out.println("IOException from logout button");
			}
		}
	}
	
}
