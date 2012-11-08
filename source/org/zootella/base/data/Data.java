package org.zootella.base.data;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Random;

import org.zootella.base.encrypt.hash.Hash;
import org.zootella.base.exception.ChopException;
import org.zootella.base.exception.DataException;

public class Data implements Comparable<Data> {

	// Make
	
	/** Make a new Data object that views the single byte y. */
	public Data(byte y) { this(Convert.toByteBuffer(y)); }
	/** Make a new Data object that views the given byte array. */
	public Data(byte[] a) { this(Convert.toByteBuffer(a)); }
	/** Make a new Data object that views the data of the given String. */
	public Data(String s) { this(Convert.toByteBuffer(s)); }
	/** Make a new Data object that views the data between b's position and limit, doesn't change b. */
	public Data(ByteBuffer b) {
		buffer = b.duplicate(); // Save a copy of b so if b's position moves, buffer's position won't
	}

	/**
	 * Make a copy of the memory this Data object views.
	 * Afterwards, the object that holds the data can close, and the copy will still view it.
	 */
	public Data copyData() {
		return (new Bay(this)).data(); // Make a new Bay that will copy the data we view into it
	}
	
	// Number and boolean as text
	
	/** Make a new Data object that holds n as text like "786". */
	public Data(long n) { this(Number.toString(n)); }
	/** Make a new Data object that holds b as the text "t" or "f". */
	public Data(boolean b) { this(b ? "t" : "f"); }

	/** Get the number in this Data, throw DataException if this Data doesn't view text numerals like "786". */
	public long toNumber() { return Number.toLong(toString()); }
	/** Get the boolean in this Data, throw DataException if this Data doesn't view the text "t" or "f". */
	public boolean toBoolean() {
		if (toString().equals("t")) return true;
		if (toString().equals("f")) return false;
		throw new DataException();
	}
	
	// Big

	/** Convert the given BigInteger into a new Data object. */
	public Data(BigInteger b) {
		this(b.toByteArray());
	}

	/** Covert this Data back into the BigInteger it was made from, or throw DataException not valid. */
	public BigInteger toBigInteger() {
		try {
			return new BigInteger(toByteArray());
		} catch (NumberFormatException e) { throw new DataException(e); }
	}

	// Convert

	/** Copy the data this Data object views into a new byte array, and return it. */
	public byte[] toByteArray() { return Convert.toByteArray(toByteBuffer()); }

	/**
	 * If you know this Data has text bytes, look at them all as a String using UTF-8 encoding.
	 * On binary data, toString() produces lines of gobbledygook but doesn't throw an exception, you may want base16() instead.
	 */
	@Override public String toString() { return Convert.toString(toByteBuffer()); }

	/**
	 * Make a ByteBuffer with position and limit clipped around the data this Data object views.
	 * You can move the position without changing this Data object.
	 */
	public ByteBuffer toByteBuffer() {
		return buffer.duplicate(); // Return a copy so if toByteBuffer()'s position changes, buffer's position won't
	}

	// Size
	
	/** The number of bytes of data this Data object views. */
	public int size() {
		return buffer.remaining(); // Measure the distance between our ByteBuffer's position and limit
	}

	/** true if this Data object is empty, it has a size of 0 bytes. */
	public boolean isEmpty() { return !hasData(); }
	/** true if this Data object views some data, it has a size of 1 or more bytes. */
	public boolean hasData() {
		return buffer.hasRemaining(); // True if our ByteBuffer's position and limit aren't closed together
	}

	// Change
	
	/**
	 * Make a new Clip object around this Data.
	 * You can remove bytes from the start of the Clip to keep track of what you've processed.
	 * The size of a Data object cannot change, while Clip can.
	 */
	public Clip clip() {
		return new Clip(this);
	}

	// Inside

	/**
	 * A Data object has a ByteBuffer buffer that clips out the data it views.
	 * The data is between buffer's position and limit.
	 * A ByteBuffer's position can move and its size can change, but Data is immutable because buffer is private and we never do that.
	 */
	private final ByteBuffer buffer;

	// Clip

	/** Clip out up to n bytes from the start of this Data. */
	@Deprecated public Data begin(int n) { //TODO remove because weird and not exact
		return start(Math.min(n, size())); // Don't try to clip out more data than we have
	}

	/** Clip out the first n bytes of this Data, start(3) is DDDddddddd. */
	public Data start(int n) { return clip(0, n); }
	/** Clip out the last n bytes of this Data, end(3) is dddddddDDD. */
	public Data end(int n) { return clip(size() - n, n); }
	/** Clip out the bytes after index i in this Data, after(3) is dddDDDDDDD. */
	public Data after(int i) { return clip(i, size() - i); }
	/** Chop the last n bytes off the end of this Data, returning the start before them, chop(3) is DDDDDDDddd. */
	public Data chop(int n) { return clip(0, size() - n); }
	/** Clip out part this Data, clip(5, 3) is dddddDDDdd. */
	public Data clip(int i, int n) {

		// Make sure the requested index and number of bytes fits inside this Data
		if (i < 0 || n < 0 || i + n > size()) throw new ChopException();

		// Make and return a new Data that clips around the requested part of this one
		ByteBuffer b = toByteBuffer(); // Make a new ByteBuffer b that looks at our data too
		b.position(b.position() + i);  // Move its position and limit inwards to clip out the requested part
		b.limit(b.position() + n);
		return new Data(b);            // Wrap a new Data object around it, and return it
	}

	/** Get the first byte in this Data. */
	public byte first() { return get(0); }
	/** Get the byte i bytes into this Data. */
	public byte get(int i) {
		if (i < 0 || i >= size()) throw new ChopException(); // Make sure i is in range
		return buffer.get(buffer.position() + i); // ByteBuffer.get() takes an index from the start of the ByteBuffer
	}

	// Same

	/** true if this Data object views a single byte, y. */
	public boolean same(byte y) { return same(new Data(y)); }
	/** true if this Data object views the same data as the given one. */
	public boolean same(Data d) {
		if (size() != d.size()) return false; // Make sure this Data and d are the same size
		else if (size() == 0) return true;    // If both are empty, they are the same
		return search(d, true, false) != -1;  // Search at the start only
	}

	// Has

	/** true if this Data starts with the byte y. */
	public boolean starts(byte y) { return starts(new Data(y)); }
	/** true if this Data starts with d. */
	public boolean starts(Data d) { return search(d, true, false) != -1; }

	/** true if this Data ends with the byte y. */
	public boolean ends(byte y) { return ends(new Data(y)); }
	/** true if this Data ends with d. */
	public boolean ends(Data d) { return search(d, false, false) != -1; }

	/** true if this Data contains the byte y. */
	public boolean has(byte y) { return has(new Data(y)); }
	/** true if this Data contains d. */
	public boolean has(Data d) { return search(d, true, true) != -1; }

	// Find

	/** Find the distance in bytes from the start of this Data to where the byte y first appears, -1 if not found. */
	public int find(byte y) { return find(new Data(y)); }
	/** Find the distance in bytes from the start of this Data to where d first appears, -1 if not found. */
	public int find(Data d) { return search(d, true, true); }

	/** Find the distance in bytes from the start of this Data to where the byte y last appears, -1 if not found. */
	public int last(byte y) { return last(new Data(y)); }
	/** Find the distance in bytes from the start of this Data to where d last appears, -1 if not found. */
	public int last(Data d) { return search(d, false, true); }

	/**
	 * Find where in this Data d appears.
	 * 
	 * @param d       The tag to search for.
	 * @param forward true to search forwards from the start.
	 *                false to search backwards from the end.
	 * @param scan    true to scan across all the positions possible in this Data.
	 *                false to only look at the starting position.
	 * @return        The byte index in this Data where d starts.
	 *                -1 if not found.
	 */
	private int search(Data d, boolean forward, boolean scan) {

		// Check the sizes
		if (d.size() == 0 || size() < d.size()) return -1;
		
		// Our search will scan this Data from the start index through the end index
		int start = forward ? 0                 : size() - d.size();
		int end   = forward ? size() - d.size() : 0;
		int step  = forward ? 1                 : -1;
		
		// If we're not allowed to scan across this Data, set end to only look one place
		if (!scan) end = start;
		
		// Scan i from the start through the end in the specified direction
		for (int i = start; i != end + step; i += step) {
			
			// Look for d at i
			int j;
			for (j = 0; j < d.size(); j++) {
				
				// Mismatch found, break to move to the next spot in this Data
				if (get(i + j) != d.get(j)) break;
			}
			
			// We found d, return the index in this Data where it is located
			if (j == d.size()) return i;
		}
		
		// Not found
		return -1;
	}

	// Split

	/** Split this Data around the given byte y, clipping out the parts before and after it. */
	public Split split(byte y) { return split(new Data(y)); }
	/** Split this Data around d, clipping out the parts before and after it. */
	public Split split(Data d) { return split(d, true); }

	/** Split this Data around the place the given byte y last appears, clipping out the parts before and after it. */
	public Split splitLast(byte y) { return splitLast(new Data(y)); }
	/** Split this Data around the place d last appears, clipping out the parts before and after it. */
	public Split splitLast(Data d) { return split(d, false); }
	
	/**
	 * Split this Data around d, clipping out the parts before and after it.
	 * 
	 * @param d       The tag to search for.
	 * @param forward true to find the first place d appears.
	 *                false to search backwards from the end.
	 * @return        A Split object that tells if d was found, and clips out the parts of this Data before and after it.
	 *                If d is not found, split.before will clip out all our data, and split.after will be empty.
	 */
	private Split split(Data d, boolean forward) {
			
		// Make a Split object to fill with answers and return
		Split split = new Split();
		
		// Search this Data for d
		int i = search(d, forward, true);
		if (i == -1) { // Not found
			split.found  = false;
			split.before = this;
			split.tag    = empty();
			split.after  = empty();
		} else {       // We found d at i, clip out the parts before and after it
			split.found  = true;
			split.before = start(i);
			split.tag    = clip(i, d.size());
			split.after  = after(i + d.size());
		}
		return split;
	}
	
	// Compare

	@Override public int compareTo(Data d) {
		return buffer.compareTo(d.buffer); // Use the methods on buffer
	}

	@Override public boolean equals(Object o) {
		if (o == null || !(o instanceof Data)) return false; // Make sure o is a Data like us
		return buffer.equals(((Data)o).buffer);
	}
	
	@Override public int hashCode() {
		return buffer.hashCode();
	}

	// Shortcut
	
	/** Encode this Data into text using base 16, each byte will become 2 characters, "00" through "ff". */
	public String base16() { return Encode.toBase16(this); }
	/** Encode this Data into text using base 32, each 5 bits will become a character a-z and 2-7. */
	public String base32() { return Encode.toBase32(this); }
	/** Encode this Data into text using base 62, each 4 or 6 bits will become a character 0-9, a-z, and A-Z. */
	public String base62() { return Encode.toBase62(this); }
	/** Encode this Data into text like --"hello"0d0a-- base 16 with text in quotes. */
	public String quote() { return Encode.quote(this); }
	/** Turn this Data into text like "hello--" striking out non-text bytes with hyphens. */
	public String strike() { return Encode.strike(this); }

	/** Compute the SHA1 hash of this Data, return the 20-byte, 160-bit hash value. */
	public Data hash() { return Hash.hash(this); }
	
	// Supply
	
	/** Make a new empty Data object that doesn't view any data, and has a size() of 0 bytes. */
	public static Data empty() {
		return new Data(ByteBuffer.allocate(0)); // Make a new Data object from a 0-byte ByteBuffer
	}

	/** A new globally unique 20 bytes of random data. */
	public static Data unique() { return random(Hash.size); }
	/** Make n bytes of random data. */
	public static Data random(int n) {
		if (random == null) random = new Random(); // Make our random number generator if we don't have it yet
		byte[] a = new byte[n];                    // Make an empty byte array n bytes long
		random.nextBytes(a);                       // Fill it with random data
		return new Data(a);                        // Wrap a new Data object around it and return it
	}

    /** Our Java random number generator. */
    private static Random random;
}
