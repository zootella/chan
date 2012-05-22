package org.everpipe.main;

import org.zootella.data.Data;
import org.zootella.state.Close;

public class Snippet {

	public static void snippet(Program program) {
		
		
		
		
		
		String s = "hello\r\n";
		
		Data d = new Data(s);
		String s2 = d.box();
		
		Close.log(s2);
		

	}
}
