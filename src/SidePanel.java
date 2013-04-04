import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class SidePanel extends JPanel implements ActionListener {
	
	JButton pencilButton;
	JButton logoutButton;
	
	DataInputStream in;
	DataOutputStream out;
	
	public SidePanel(DataInputStream in, DataOutputStream out){
		setOpaque(false);
		pencilButton = new JButton("P");
		logoutButton = new JButton("X");
		
		pencilButton.addActionListener(this);
		logoutButton.addActionListener(this);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(pencilButton);
		add(Box.createRigidArea(new Dimension(10, 10)));
		add(logoutButton);
		
		this.in = in;
		this.out = out;
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == logoutButton){
			byte[] bytes = new byte[32];
			bytes[0] = 0;
			try{
				out.write(bytes);
			}
			catch(IOException ioe){
				System.out.println("IOException from logout button");
			}
		}
	}
	
}
