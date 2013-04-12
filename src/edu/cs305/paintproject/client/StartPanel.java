package edu.cs305.paintproject.client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import edu.cs305.paintproject.Constants;
import edu.cs305.paintproject.server.PictureRequest;
import edu.cs305.paintproject.util.Logger;

public class StartPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7474434029050120495L;
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
		createNew.addActionListener(this);
		
		right.add(scrollPane);
		right.add(submit);
		
		add(left);
		add(right);
		
		try{
			out.writeObject(Constants.REQUEST_PICTURE_NAMES);
			out.flush();
		}
		catch(IOException ioe){
			Logger.log(ioe,"while requesting picture names...");
		}
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == submit){
			try{
				out.writeObject(new PictureRequest(list.getSelectedValue()));
				out.flush();
			}
			catch(IOException ioe){
				Logger.log(ioe,"while requesting picture files...");
			}
		}else if(e.getSource() == createNew)
		{
			try{
				out.writeObject(new PictureRequest(pictureName.getText()));
				out.flush();
			}
			catch(IOException ioe){
				Logger.log(ioe,"while requesting picture creation...");
			}
		}
	}
}
