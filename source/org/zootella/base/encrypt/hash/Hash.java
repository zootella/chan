package org.zootella.base.encrypt.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.zootella.base.data.Data;
import org.zootella.base.data.Value;
import org.zootella.base.exception.PlatformException;
import org.zootella.base.state.Close;

public class Hash extends Close {
	
	// Object
	
	/** Make a Hash object to compute the SHA1 hash of some data. */
	public Hash() {
		try {
			digest = get();                                // Look in list for a recycled digest object
			if (digest == null)                            // Not found, we'll have to make a new one
				digest = MessageDigest.getInstance("SHA"); // Ask for the SHA1 algorithm
		} catch (NoSuchAlgorithmException e) { throw new PlatformException(e); }
	}
	
	/** Our MessageDigest object that hashes more data based on what has come before. */
	private MessageDigest digest;
	
	/** Get the 20 byte, 160 bit SHA1 hash value, or null before we're done. */
	public Value value() { return value; }
	private Value value;

	@Override public void close() {
		if (already()) return;
		value = new Value(new Data(digest.digest())); // Calling digest() gets the hash value and resets the digest object
		add(digest);   // Save the reset digest in our list to use it quickly next time
		digest = null;
	}

	/** Hash the next block of data, how you cut it up doesn't matter, just give add() the blocks in order. */
	public void add(Data data) {
		confirmOpen();
		digest.update(data.toByteBuffer());
	}
	
	/** When you have added all the data, call done() to get the result. */
	public Value done() {
		close(this);
		return value;
	}
	
	// Recycle
	
	/** Keep a finished digest object. */
	private static synchronized void add(MessageDigest digest) { // Synchronized so the event thread and Task threads can recycle objects here
		if (list.size() >= capacity) return; // We're full, don't keep it
		list.add(digest);
	}

	/** Quickly get a reset digest object, or return null if we're empty. */
	private static synchronized MessageDigest get() { // Synchronized so the event thread and Task threads can get Hash objects from us
		if (list.isEmpty()) return null; // Fresh out, have the caller allocate a new one
		return list.remove(0);           // Return one from our list
	}
	
	/** Reset MessageDigest objects to use later. */
	private static final List<MessageDigest> list = new ArrayList<MessageDigest>();
	
	/** Maximum number of recycled objects the program can hold. */
	private static final int capacity = 4;
}
