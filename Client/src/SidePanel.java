import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SidePanel extends JPanel implements ActionListener {
	
	enum Operation {Pencil, Bucket}; 
	Operation op;
	
	JButton pencilButton;
	JButton bucketButton;
	JButton logoutButton;
	
	DataInputStream in;
	DataOutputStream out;
	
	public SidePanel(DataInputStream in, DataOutputStream out){
		setOpaque(false);
		
		pencilButton = addNewButton("Paint");
		logoutButton = addNewButton("Logout");
		bucketButton = addNewButton("Bucket");		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(Box.createRigidArea(new Dimension(10, 10)));
		
		this.in = in;
		this.out = out;
	}
	
	private JButton addNewButton(String name)
	{
		JButton btn = new JButton(name);
		btn.addActionListener(this);
		this.add(btn);
		return btn;
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == logoutButton){
			byte[] bytes = new byte[32];
			bytes[0] = 0;
			try{
				out.write(bytes);
				System.exit(0);
			}
			catch(IOException ioe){
				System.out.println("IOException from logout button");
			}
		}
		else if(e.getSource() == pencilButton)
			op = Operation.Pencil;
		else if(e.getSource() == bucketButton)
			op = Operation.Bucket;
	}
	
}
