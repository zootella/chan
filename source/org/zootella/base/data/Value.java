package org.zootella.base.data;

import org.zootella.base.exception.DataException;

/** 20 bytes of data, like a hash value or a GUID. */
public class Value {
	
	// Define
	
	/** A value is 20 bytes, which is 160 bits. */
	public static final int size = 20;
	
	// Object
	
	/** Wrap the given data in this new Value object, which makes sure it is 20 bytes. */
	public Value(Data data) {
		if (data.size() != size) throw new DataException();
		this.data = data;
	}

	/** The 20 bytes of data this Value object holds. */
	public final Data data;

	// Unique

	/** Make a new globally unique value with 20 bytes of random data. */
	public static Value unique() { return new Value(Data.random(size)); }
}
