import java.io.*;

public class PictureRequest implements Serializable {
	
	String pictureName;
	
	public PictureRequest(String pictureName){
		this.pictureName = pictureName;
	}
}
