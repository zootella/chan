package org.zootella.base.process;

import org.zootella.base.data.Text;
import org.zootella.base.time.Now;

public class Log {
	
	// Log
	public static void log(String... strings) {
		System.out.println((new Now()).toString() + " " + Text.add(strings));
	}
}
