package edu.cs305.paintproject.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	public static final boolean PRINT_MSGS = true;
	public static final boolean PRINT_STACK = true;

	private static SimpleDateFormat date_format = new SimpleDateFormat("[MMM.dd HH:mm:ss] ");

	private static void putLine(String line)
	{
		Date now = new Date(System.currentTimeMillis());
		System.err.print(date_format.format(now));
		System.err.println(line);
	}
	
	public static void log(String... strs)
	{
		StringBuilder sb = new StringBuilder();
		if(strs.length > 1)sb.append('[');
		for(String str : strs)
		{
			sb.append(str);
			sb.append(", ");
		}
		sb.delete(sb.length()-2, sb.length());
		if(strs.length > 1)sb.append(']');
		putLine(sb.toString());
	}
	
	public static void log(Object... objs)
	{
		String[] strs = new String[objs.length];
		for(int i = 0; i<objs.length; i++)strs[i] = objs[i].toString();
		log(strs);
	}
	
	public static void log(Exception e)
	{
		if(PRINT_MSGS)log(e.toString());
		if(PRINT_STACK)e.printStackTrace(System.err);
	}
	
	public static void log(Exception e, String msg)
	{
		if(PRINT_MSGS)log(e + " " + msg);
		if(PRINT_STACK)e.printStackTrace(System.err);
	}
}
