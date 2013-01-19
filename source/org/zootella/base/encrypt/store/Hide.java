package org.zootella.base.encrypt.store;

import org.zootella.base.data.Bay;
import org.zootella.base.data.Data;
import org.zootella.base.encrypt.hash.Hash;

/** Hide a 20 byte value by hashing it with some base data. */
public class Hide {
	
	/** 256, number of values in a byte, size of the base data to hash plain values with. */
	public static final int baseSize = -1*(int)Byte.MIN_VALUE + 1 + (int)Byte.MAX_VALUE;

	/** Generate the base data for the store, we'll hash it along with the store names. */
	public static Data makeBase() {
		return Data.random(baseSize);
	}

	/** Hide plain by hashing it with part of base. */
	public static Data hide(Data base, Data plain) {
		if (base.size() != baseSize || plain.size() != Hash.size) throw new IllegalArgumentException();//TODO shouldn't this be a kind of program exception?

		// Use the first byte of plain to determine what part of base to clip out
		int index = (int)plain.get(0) + -1*(int)Byte.MIN_VALUE; // The byte can be -128 through 127, so add 128 to make index 0 through 255

		// Compose data to hash
		Bay bay = new Bay();
		bay.add(plain); // It will be the given plain hash, and then part of base
		if (index + Hash.size > baseSize) { // The index goes over the far edge of base
			bay.add(base.clip(index, baseSize - index));
			bay.add(base.clip(0, Hash.size - (baseSize - index))); // Get the portion that loops around
		} else { // The index fits in base
			bay.add(base.clip(index, Hash.size));
		}
		
		// Hash the given plain hash with the part of base we clipped out
		return bay.data().hash();
	}
}
