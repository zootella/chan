package org.zootella.main;

import org.zootella.base.data.Data;
import org.zootella.base.data.Encode;


public class Snippet {

	public static void snippet(Program program) {
		
		String s = Encode.quote(new Data("\tyes\t"));
		Data d = Encode.unquote(s);
		
	}
}
