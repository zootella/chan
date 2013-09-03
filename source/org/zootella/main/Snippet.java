package org.zootella.main;

import org.zootella.base.data.Data;
import org.zootella.base.data.Encode;
import org.zootella.base.process.Log;

public class Snippet {

	public static void snippet(Program program) {
		
		/*
		Data d = new Data("hello\r\n");
		Log.log(d.quote());
		*/
		
		//see what it's like to encrypt and decrypt really small bits of data
		//small files will be like this, as well as small collections
		//for instance, the hash library of every sub1mb file will be just 20 bytes
		//do you need a salt or soemthing to protect that?
		
		/*
		Data d = Encode.fromBase16("89ab");
		Log.log(d.base32());
		*/
		
		
		
	}
	
}
	
	
	
	


