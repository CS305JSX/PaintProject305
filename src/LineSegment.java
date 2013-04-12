import java.io.*;

public class LineSegment implements Serializable {
	private static final long serialVersionUID = -2941428897802405283L;
	public int x1, x2, y1, y2;
	
	public LineSegment(int x1, int y1, int x2, int y2){
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public String toString(){
		return x1 + "";
	}
	
}
