package edu.cs305.paintproject;
import java.io.*;

public class PictureRequest implements Serializable {
	private static final long serialVersionUID = 2895156601249215621L;
	public String pictureName;
	
	public PictureRequest(String pictureName){
		this.pictureName = pictureName;
	}
}
