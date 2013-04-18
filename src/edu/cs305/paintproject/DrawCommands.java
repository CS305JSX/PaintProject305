package edu.cs305.paintproject;
import java.awt.Color;
import java.awt.Graphics;

import edu.cs305.paintproject.util.Vec2D;

public class DrawCommands {
	
	public static void drawCommand(Object command, Graphics g){
		if(command instanceof LineSegment){
			drawLineSegmentWithWidth((LineSegment)command, g);
		}
	}
	
	public static void drawLineSegment(LineSegment line, Graphics g){
		g.setColor(Color.black);
		g.drawLine(line.x1, line.y1, line.x2, line.y2);
	}
	
	public static void drawLineSegmentWithWidth(LineSegment line, Graphics g)
	{
		int width = line.width & 0xFF;
		g.setColor(new Color(line.color));
		
		int xp[] = new int[4];
		int yp[] = new int[4];
		
		Vec2D st = new Vec2D(line.x1,line.y1);
		Vec2D ed = new Vec2D(line.x2,line.y2);
		Vec2D dist = ed.subtracted(st).normalized().multiplied(width/2f);
		
		Vec2D p = st.added(dist.cwiseNormal());
		xp[0] = (int) p.x;
		yp[0] = (int) p.y;
		
		p = st.added(dist.ccwiseNormal());
		xp[1] = (int) p.x;
		yp[1] = (int) p.y;
		
		p = ed.added(dist.cwiseNormal());
		xp[3] = (int) p.x;
		yp[3] = (int) p.y;
		
		p = ed.added(dist.ccwiseNormal());
		xp[2] = (int) p.x;
		yp[2] = (int) p.y;
		
		g.fillOval(line.x1 - width/2, line.y1 - width/2, width, width);
		g.fillOval(line.x2 - width/2, line.y2 - width/2, width, width);
		g.fillPolygon(xp, yp,4);
	}
}
