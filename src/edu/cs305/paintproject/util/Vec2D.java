package edu.cs305.paintproject.util;

public class Vec2D {

	public double x,y;
	
	public Vec2D (double x,double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double length2()
	{
		return x*x + y*y;
	}
	
	public double length()
	{
		return Math.sqrt(length2());
	}
	
	public Vec2D normalized()
	{
		double len = length();
		
		
		return new Vec2D(x/len, y/len);
	}
	
	public Vec2D multiplied(float len)
	{
		return new Vec2D(x*len, y*len);
	}
	
	public Vec2D cwiseNormal()
	{
		return new Vec2D(-y, x);
	}
	
	public Vec2D ccwiseNormal()
	{
		return new Vec2D(y, -x);
	}
	
	public Vec2D subtracted(Vec2D vec)
	{
		return new Vec2D(x - vec.x, y - vec.y);
	}
	
	public Vec2D added(Vec2D vec)
	{
		return new Vec2D(x + vec.x, y + vec.y);
	}
}
