import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class StartPanel extends JPanel implements ActionListener {
	
	JPanel left, right;
	JButton createNew, submit;
	JTextField pictureName;
	JList<String> list;
	JScrollPane scrollPane;
	
	ObjectInputStream in;
	ObjectOutputStream out;
	PaintFrame frame;
	
	public StartPanel(ObjectInputStream in, ObjectOutputStream out, PaintFrame frame){
		this.in = in;
		this.out = out;
		this.frame = frame;
		
		left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		pictureName = new JTextField("Title");
		createNew = new JButton("Create New Picture");
		left.add(pictureName);
		left.add(createNew);
		
		
		right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		list = new JList<String>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		
		scrollPane = new JScrollPane(list);
		
		submit = new JButton("Submit");
		submit.addActionListener(this);
		
		right.add(scrollPane);
		right.add(submit);
		
		add(left);
		add(right);
		
		try{
			out.writeObject(Constants.REQUEST_PICTURE_NAMES);
			out.flush();
		}
		catch(IOException ioe){
			System.out.println("IOException while requesting picture names...");
		}
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == submit){
			try{
				out.writeObject(new PictureRequest(list.getSelectedValue()));
				out.flush();
			}
			catch(IOException ioe){
				System.out.println("IOException while requesting picture file...");
			}
		}
	}
}
