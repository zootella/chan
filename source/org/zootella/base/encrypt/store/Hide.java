package org.zootella.base.encrypt.store;

import org.zootella.base.data.Bay;
import org.zootella.base.data.Convert;
import org.zootella.base.data.Data;
import org.zootella.base.data.Value;
import org.zootella.base.exception.DataException;

/** Hide a 20 byte value by hashing it with some base data. */
public class Hide {
	
	/** 256, number of values in a byte, size of the base data to hash plain values with. */
	public static final int baseSize = -1*(int)Byte.MIN_VALUE + 1 + (int)Byte.MAX_VALUE;

	/** Generate the base data for the store, we'll hash it along with the store names. */
	public static Data makeBase() {
		return Data.random(baseSize);
	}

	/** Hide plain by hashing it with part of base. */
	public static Value hide(Data base, Value plain) {
		if (base.size() != baseSize) throw new DataException();

		// Use the first byte of plain to determine what part of base to clip out
		int index = Convert.byteToUnsigned(plain.data.first());

		// Compose data to hash
		Bay bay = new Bay();
		bay.add(plain.data); // It will be the given plain hash, and then part of base
		if (index + Value.size > baseSize) { // The index goes over the far edge of base
			bay.add(base.clip(index, baseSize - index));
			bay.add(base.clip(0, Value.size - (baseSize - index))); // Get the portion that loops around
		} else { // The index fits in base
			bay.add(base.clip(index, Value.size));
		}
		
		// Hash the given plain hash with the part of base we clipped out
		return bay.data().hash();
	}
}
