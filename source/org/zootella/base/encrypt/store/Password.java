package org.zootella.base.encrypt.store;

import org.zootella.base.data.Bay;
import org.zootella.base.data.Data;

/** Use a password from the user to scramble and unscramble some longer data, like a symmetric key. */
public class Password {

	/** Tell the user if their password is strong enough. */
	public boolean strong(String password) {
		return false; //TODO, return true if 12+, upper, lower, number, and other
	}

	/** Encrypt data using password. */
	public static Data scramble(Data data, String password) {
		return shift(data, expand(password, data.size()), true); // Add to shift each byte forward
	}
	
	/** Recover the data you encrypted by providing the same password. */
	public static Data unscramble(Data data, String password) {
		return shift(data, expand(password, data.size()), false); // Subtract to shift the bytes back
	}
	
	/** Produce size bytes of data that doesn't repeat by hashing the given password. */
	private static Data expand(String password, int size) {
		
		Bay result = new Bay();   // The finished data
		Bay tray   = new Bay();   // A temporary tray to fill, hash, and clear
		Data hash = Data.empty(); // The most recent hash value
		boolean use = false;      // True after the first loop
		
		while (result.size() < size) { // Loop until we produced enough data
			
			tray.clear();
			tray.add(hash);
			tray.add(password);
			hash = tray.data().hash(); // Hash the last hash and the password
			
			if (use) result.add(hash); // Add it to the result
			use = true; // Don't start with the hash of the password
		}
		return result.data().start(size); // Return the size requested
	}
	
	/** Add or subtract the bytes of pass from the bytes of data. */
	private static Data shift(Data data, Data pass, boolean add) {
		if (data.size() != pass.size()) throw new IllegalArgumentException(); // Must be same size
		
		Bay result = new Bay(); // Make a new empty bay to hold the result data
		for (int i = 0; i < data.size(); i++) { // Loop for each byte
			
			byte b; // Shifted byte
			if (add) b = (byte)(data.get(i) + pass.get(i)); // Works because it loops around
			else     b = (byte)(data.get(i) - pass.get(i));
			result.add(b);
		}
		return result.data(); // Return the data with the shifted byte results
	}
}
