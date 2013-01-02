package org.zootella.base.encrypt.small;

import org.zootella.base.data.Bay;
import org.zootella.base.data.Data;
import org.zootella.base.encrypt.hash.Hash;

public class Small {
	
	
	
	/** number of values in a byte, and size of the random hash key. */
	public static final int gardenSize = 256;
	
	
	public static Data makeGarden() {
		
		return Data.random(gardenSize);
	}
	
	public static Data hide(Data garden, Data plain) {
		if (garden.size() != gardenSize || plain.size() != Hash.size) throw new IllegalArgumentException();//TODO make this a program exception of some kind
		
		//the byte can have a value of -128 through 127, we want an index of 0 through 255, so add 128
		int index = (int)plain.get(0) + (-1 * Byte.MIN_VALUE);
		
		/*
		
		Bay bay = new Bay();
		bay.add(plain);

		Data gardenPart;
		if (index + Hash.size > gardenSize) {
			
		} else {
			gardenPart = garden.clip(index, n)
			
		}
		
		Data part = garden.clip(i, n);
		*/
		
		
		return Data.empty();
	}
	
	
	//circular clip
	
	public static Data circleClip(Data d, int i, int n) {
		Bay bay = new Bay();
		
		int size = d.size();
		
		for (int loop = 0; loop < n; loop++) {
			
			
		}
		
		return Data.empty();
	}
}
