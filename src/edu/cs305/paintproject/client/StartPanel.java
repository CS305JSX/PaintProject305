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
import edu.cs305.paintproject.PictureRequest;
import edu.cs305.paintproject.util.Logger;

public class StartPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7474434029050120495L;
	JPanel left, right;
	JButton createNew, submit;
	JTextField pictureName;
	JList list;
	JScrollPane scrollPane;
	
	PaintFrame frame;
	
	public StartPanel(PaintFrame frame){
		this.frame = frame;
		
		left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		pictureName = new JTextField("Title");
		createNew = new JButton("Create New Picture");
		left.add(pictureName);
		left.add(createNew);
		
		
		right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		list = new JList();
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
		
		frame.msm.sendRequestPictureNames();
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == submit){
			frame.msm.sendRequestPicture(new PictureRequest((String)(list.getSelectedValue())));
		}
		else if(e.getSource() == createNew)
		{
			Logger.log("Create Picture button pressed");
			frame.msm.sendRequestPicture(new PictureRequest(pictureName.getText()));
		}
	}
}
