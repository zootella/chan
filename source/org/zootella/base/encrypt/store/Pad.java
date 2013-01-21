package org.zootella.base.encrypt.store;

import org.zootella.base.data.Bay;
import org.zootella.base.data.Clip;
import org.zootella.base.data.Convert;
import org.zootella.base.data.Data;

public class Pad {
	
	/** Generate a pad of random data 1 to 256 bytes long. */
	public static Data create() {
		Bay bay = new Bay();
		bay.add(Data.random(1));
		bay.add(Data.random(Convert.byteToUnsigned(bay.data().first())));
		return bay.data();
	}
	
	/** Remove a pad of random data from the start of clip, or throw ChopException. */
	public static void parse(Clip clip) {
		int size = Convert.byteToUnsigned(clip.data().first()); // Read the value of the first byte, 0 through 255
		clip.remove(1 + size); // Remove the first byte and that many more after it
	}
}
