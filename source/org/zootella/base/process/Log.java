package org.zootella.base.process;

import org.zootella.base.time.Now;

public class Log {
	
	// Log
	public static void log(String s) { System.out.println((new Now()).toString() + " " + s); }
}
