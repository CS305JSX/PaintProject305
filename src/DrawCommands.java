import java.awt.*;

public class DrawCommands {
	
	public static void drawCommand(Object command, Graphics g){
		if(command instanceof LineSegment){
			drawLineSegment((LineSegment)command, g);
		}
	}
	
	public static void drawLineSegment(LineSegment line, Graphics g){
		g.setColor(Color.black);
		g.drawLine(line.x1, line.y1, line.x2, line.y2);
	}
	
}
