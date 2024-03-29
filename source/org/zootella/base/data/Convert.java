package org.zootella.base.data;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.zootella.base.exception.PlatformException;

public class Convert {

	// Byte

	/** Convert a single byte into a new byte array. */
	public static byte[] toByteArray(byte y) {
		byte[] a = new byte[1]; // Make a new byte array that has a size of 1 byte
		a[0] = y;               // Put the given byte in it, and return it
		return a;
	}

	/** Convert a byte like 'a' into the String "a". */
	public static String toString(byte y) {
		return toString(toByteBuffer(y)); // Go through ByteBuffer
	}

	/** Convert a single byte into data in a ByteBuffer. */
	public static ByteBuffer toByteBuffer(byte y) {
		return toByteBuffer(toByteArray(y)); // Go through byte array
	}
	
	// Byte array
	
	/**
	 * Convert a byte array to a String using UTF-8 encoding.
	 * Turns an empty byte array like new byte[0] into a blank String like "".
	 */
	public static String toString(byte[] a) {
		String s = null;
		try { s = new String(a, encoding); } catch (UnsupportedEncodingException e) { throw new PlatformException(e); }
		return s;
	}
	
	/** Wrap a ByteBuffer object around a given byte array, position and limit will clip around the whole thing. */
	public static ByteBuffer toByteBuffer(byte[] a) {
		return ByteBuffer.wrap(a); // This doesn't copy any data
	}
	
	// String

	/** Convert a String into a byte array using UTF-8 encoding. */
	public static byte[] toByteArray(String s) {
		byte[] a = null;
		try { a = s.getBytes(encoding); } catch (UnsupportedEncodingException e) { throw new PlatformException(e); }
		return a;
	}
	
	/** Convert a String into data in a ByteBuffer, using UTF-8 encoding. */
	public static ByteBuffer toByteBuffer(String s) {
		return toByteBuffer(toByteArray(s)); // Go through byte array
	}
	
	// ByteBuffer

	/** Copy the data in a ByteBuffer into a new byte array. */
	public static byte[] toByteArray(ByteBuffer b) {
		byte[] a = new byte[b.remaining()]; // Make a new empty byte array that's the right size
		b.duplicate().get(a);               // Copy data from b to a, duplicate b to not move its position
		return a;
	}
	
	/** Convert the data in a ByteBuffer to a String using UTF-8 encoding. */
	public static String toString(ByteBuffer b) {
		return toString(toByteArray(b)); // Go through byte array
	}

	// Define
	
	/** ASCII with Unicode characters mixed in. */
	public static final String encoding = "UTF-8";
	
	// Byte
	
	/** Given a byte -128 through 127, add 128 to return an int 0 through 255. */
	public static int byteToUnsigned(byte b) {
		return (int)b + -1*(int)Byte.MIN_VALUE;
	}
}
