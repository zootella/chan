package org.zootella.main;

import org.zootella.base.data.Data;
import org.zootella.base.process.Log;

public class Snippet {

	public static void snippet(Program program) {
		
		Data d = new Data("hello\r\n");
		Log.log(d.quote());
	}
}
	
	
	
	


