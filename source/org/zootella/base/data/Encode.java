package org.zootella.base.data;

import org.zootella.base.exception.DataException;

// document which methods are reversible, and which are one way
// which are readable by the user, and which are not
// which are good for data that is mostly ascii text
// which keep the same length, which grow, and by how much
// which always expand or contract by the same amount, and which grow depending on contents
// talk about String, Data, Bay, and StringBuffer, and the shortcut methods
// to throws CodeException, while from throws DataException

/** Use TextEncode methods to convert data to and from text letters and numbers using base 16, 32, and 62 encoding. */
public class Encode {
	
	// Shortcuts

	/** Turn data into text using base 16, each byte will become 2 characters, "00" through "ff". */
	public static String toBase16(Data d) { StringBuffer b = new StringBuffer(); toBase16(b, d); return b.toString(); }
	/** Turn data into text using base 32, each 5 bits will become a character a-z and 2-7. */
	public static String toBase32(Data d) { StringBuffer b = new StringBuffer(); toBase32(b, d); return b.toString(); }
	/** Turn data into text using base 62, each 4 or 6 bits will become a character 0-9, a-z, and A-Z. */
	public static String toBase62(Data d) { StringBuffer b = new StringBuffer(); toBase62(b, d); return b.toString(); }
	
	/** Turn base 16-encoded text back into the data it was made from. */
	public static Data fromBase16(String s) { Bay bay = new Bay(); fromBase16(bay, s); return bay.data(); }
	/** Turn base 32-encoded text back into the data it was made from. */
	public static Data fromBase32(String s) { Bay bay = new Bay(); fromBase32(bay, s); return bay.data(); }
	/** Turn base 62-encoded text back into the data it was made from. */
	public static Data fromBase62(String s) { Bay bay = new Bay(); fromBase62(bay, s); return bay.data(); }
	
	/** Turn data into text using base 16, and put text characters in quotes, --The quote " character\r\n-- becomes --"The quote "22" character"0d0a-- */
	public static String quote(Data d) { StringBuffer b = new StringBuffer(); quote(b, d); return b.toString(); }
	/** Turn quoted text back into the data it was made from. */
	public static Data unquote(String s) { Bay bay = new Bay(); unquote(bay, s); return bay.data(); }

	/** Turn data into text like "hello--", striking out non-text bytes with hyphens. */
	public static String strike(Data d) { StringBuffer b = new StringBuffer(); strike(b, d); return b.toString(); }
	
	// Base 16, 32, and 62

	/** Turn data into text using base 16, each byte will become 2 characters, "00" through "ff". */
	public static void toBase16(StringBuffer b, Data d) {
			
		// Use 0-9 and a-f, 16 different characters, to describe the data
		String alphabet = "0123456789abcdef";
		
		// Loop through each byte in the data
		for (int i = 0; i < d.size(); i++) {
			
			// Encode the byte into 2 characters
			b.append(alphabet.charAt((d.get(i) & 0xff) >> 4)); // Shift right 4 bits to read just the first part 1001----
			b.append(alphabet.charAt((d.get(i) & 0xff) & 15)); // Mask with 15 1111 to read just the second part ----1001
		}
	}

	/** Turn data into text using base 32, each 5 bits will become a character a-z and 2-7. */
	public static void toBase32(StringBuffer b, Data d) {

		// Use a-z and 2-7, 32 different characters, to describe the data
		String alphabet = "abcdefghijklmnopqrstuvwxyz234567"; // Base 32 encoding omits 0 and 1 because they look like uppercase o and lowercase L
		
		// Loop through the memory, encoding its bits into letters and numbers
		int byteIndex, bitIndex;                    // The bit index i as a distance in bytes followed by a distance in bits
		int pair, mask, code;                       // Use the data bytes a pair at a time, with a mask of five 1s, to read a code 0 through 31
		for (int i = 0; i < d.size() * 8; i += 5) { // Move the index in bits forward across the memory in steps of 5 bits
			
			// Calculate the byte and bit to move to from the bit index
			byteIndex = i / 8; // Divide by 8 and chop off the remainder to get the byte index
			bitIndex  = i % 8; // The bit index within that byte is the remainder
			
			// Copy the two bytes at byteIndex into pair
			pair = (d.get(byteIndex) & 0xff) << 8; // Copy the byte at byteindex into pair, shifted left to bring eight 0s on the right
			if (byteIndex + 1 < d.size()) pair |= (d.get(byteIndex + 1) & 0xff); // On the last byte, leave the right byte in pair all 0s
			
			// Read the 5 bits at i as a number, called code, which will be 0 through 31
			mask = 31 << (11 - bitIndex);   // Start the mask 11111 31 shifted into position      0011111000000000
			code = pair & mask;             // Use the mask to clip out just that portion of pair --10101---------
			code = code >> (11 - bitIndex); // Shift it to the right to read it as a number       -----------10101
			
			// Describe the 5 bits with a numeral or letter
			b.append(alphabet.charAt(code));
		}
	}

	/** Turn data into text using base 62, each 4 or 6 bits will become a character 0-9, a-z, and A-Z. */
	public static void toBase62(StringBuffer b, Data d) {
			
		// Use 0-9, a-z and A-Z, 62 different characters, to describe the data
		String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		
		// Loop through the memory, encoding its bits into letters and numbers
		int i = 0;                 // The index in bits, from 0 through all the bits in the given data
		int byteIndex, bitIndex;   // The same index as a distance in bytes followed by a distance in bits
		int pair, mask, code;      // Use the data bytes a pair at a time, with a mask of six 1s, to read a code 0 through 63
		while (i < d.size() * 8) { // When the bit index moves beyond the memory, we're done
			
			// Calculate the byte and bit to move to from the bit index
			byteIndex = i / 8; // Divide by 8 and chop off the remainder to get the byte index
			bitIndex  = i % 8; // The bit index within that byte is the remainder
			
			// Copy the two bytes at byteIndex into pair
			pair = (d.get(byteIndex) & 0xff) << 8; // Copy the byte at byteindex into pair, shifted left to bring eight 0s on the right
			if (byteIndex + 1 < d.size()) pair |= (d.get(byteIndex + 1) & 0xff); // On the last byte, leave the right byte in pair all 0s
			
			// Read the 6 bits at i as a number, called code, which will be 0 through 63
			mask = 63 << (10 - bitIndex);   // Start the mask 111111 63 shifted into position     0011111100000000
			code = pair & mask;             // Use the mask to clip out just that portion of pair --101101--------
			code = code >> (10 - bitIndex); // Shift it to the right to read it as a number       ----------101101
			
			// Describe the 6 bits with a numeral or letter, 111100 is 60 and Y, if more than that use Z and move forward 4, not 6
			if (code < 61) { b.append(alphabet.charAt(code)); i += 6; } // 000000  0 '0' through 111100 60 'Y'
			else           { b.append(alphabet.charAt(61));   i += 4; } // 111101 61, 111110 62, and 111111 63 are 'Z', move past the four 1s
		}
	}

	/** Turn base 16-encoded text back into the data it was made from. */
	public static void fromBase16(Bay bay, String s) {

		// Loop for each character in the text
		char c;       // The character we are converting into bits
		int code;     // The 4 bits the character gets turned into
		int hold = 0; // A place to hold bits from 2 characters until we have 8 bits and can write a byte
		for (int i = 0; i < s.length(); i++) {

			// Get a character from the text, and convert it into its code
			c = Character.toUpperCase(s.charAt(i));             // Accept uppercase and lowercase letters
			if      (c >= '0' && c <= '9') code = c - '0';      // '0'  0 0000 through '9'  9 1001
			else if (c >= 'A' && c <= 'F') code = c - 'A' + 10; // 'A' 10 1010 through 'F' 15 1111
			else throw new DataException();                     // Invalid character

			// This is the first character in a pair
			if (i % 2 == 0) {

				// Shift the 4 bytes it means into the high portion of the byte, like 1000----
				hold = code << 4;

			// This is the second character in a pair
			} else {

				// Copy the 4 bits from the second character in the pair into the low portion of the byte, like ----1100
				hold |= code; // Use the bitwise or operator to assemble the entire byte, like 10001100

				// Add the byte we made
				bay.add((byte)hold);
			}
		}
	}

	/** Turn base 32-encoded text back into the data it was made from. */
	public static void fromBase32(Bay bay, String s) {

		// Loop for each character in the text
		char c;        // The character we are converting into bits
		int  code;     // The bits the character gets turned into
		int  hold = 0; // A place to hold bits from several characters until we have 8 and can write a byte
		int  bits = 0; // The number of bits stored in the right side of hold right now
		for (int i = 0; i < s.length(); i++) {

			// Get a character from the text, and convert it into its code
			c = Character.toUpperCase(s.charAt(i));             // Accept uppercase and lowercase letters
			if      (c >= 'A' && c <= 'Z') code = c - 'A';      // 'A'  0 00000 through 'Z' 25 11001
			else if (c >= '2' && c <= '7') code = c - '2' + 26; // '2' 26 11010 through '7' 31 11111
			else throw new DataException();                  // Invalid character

			// Insert the bits from code into hold
			hold = (hold << 5) | code; // Shift the bits in hold to the left 5 spaces, and copy in code there
			bits += 5;                 // Record that there are now 5 more bits being held

			// If we have enough bits in hold to write a byte
			if (bits >= 8) {

				// Move the 8 leftmost bits in hold to our Bay object
				bay.add((byte)(hold >> (bits - 8)));
				bits -= 8; // Remove the bits we wrote from hold, any extra bits there will be written next time
			}
		}
	}

	/** Turn base 62-encoded text back into the data it was made from. */
	public static void fromBase62(Bay bay, String s) {

		// Loop for each character in the text
		char c;        // The character we are converting into bits
		int  code;     // The bits the character gets turned into
		int  hold = 0; // A place to hold bits from several characters until we have 8 and can write a byte
		int  bits = 0; // The number of bits stored in the right side of hold right now
		for (int i = 0; i < s.length(); i++) {

			// Get a character from the text, and convert it into its code
			c = s.charAt(i);
			if      (c >= '0' && c <= '9') code = c - '0';      // '0'  0 000000 through '9'  9 001001
			else if (c >= 'a' && c <= 'z') code = c - 'a' + 10; // 'a' 10 001010 through 'z' 35 100011
			else if (c >= 'A' && c <= 'Y') code = c - 'A' + 36; // 'A' 36 100100 through 'Y' 60 111100
			else if (c == 'Z')             code = 61;           // 'Z' indicates 61 111101, 62 111110, or 63 111111 are next, we will just write four 1s
			else throw new DataException();                  // Invalid character

			// Insert the bits from code into hold
			if (code == 61) { hold = (hold << 4) | 15;   bits += 4; } // Insert 1111 for 'Z'
			else            { hold = (hold << 6) | code; bits += 6; } // Insert 000000 for '0' through 111100 for 'Y'

			// If we have enough bits in hold to write a byte
			if (bits >= 8) {

				// Move the 8 leftmost bits in hold to our Bay object
				bay.add((byte)(hold >> (bits - 8)));
				bits -= 8; // Remove the bits we wrote from hold, any extra bits there will be written next time
			}
		}
	}
	
	// Quote

	/** Turn data into text using base 16, and put text characters in quotes, --The quote " character\r\n-- becomes --"The quote "22" character"0d0a-- */
	public static void quote(StringBuffer b, Data d) {
		
		if (!moreText(d)) { // The given data is mostly data bytes, like random data
			toBase16(b, d); // Present it as a single block of base 16 without quoting out the text it may contain
			return;
		}
		
		Clip clip = d.clip();    // Clip around d to remove what we've encoded
		while (clip.hasData()) { // Loop until clip is empty
			if (isText(clip.data().first())) {
				b.append('\"');
				b.append(clip.cut(count(clip.data(), true)).toString()); // Surround bytes that are text characters with quotes				
				b.append('\"');
				
			} else {
				Encode.toBase16(b, clip.cut(count(clip.data(), false))); // Encode other bytes into base 16 outside the quotes
			}
		}
	}
	
	/** Turn quoted text back into the data it was made from. */
	public static void unquote(Bay bay, String s) {
		while (Text.is(s)) { // Loop until we're out of source text
			
			Split<String> q1 = Text.split(s, "\""); // Split on the first opening quote to look for bytes before text

			Split<String> c = Text.split(q1.before, "#");        // Look for a comment outside the quotes
			if (c.found) {                                   // Found a comment
				bay.add(Encode.fromBase16(c.before.trim())); // Only bytes and spaces can be before the comment
				return;                                      // Hitting a comment means we're done with the line
			}
				
			bay.add(Encode.fromBase16(q1.before)); // Only bytes can be before the opening quote
			if (!q1.found) return;                 // No opening quote, so we got it all
			
			Split<String> q2 = Text.split(q1.after, "\""); // Split on the closing quote
			if (!q2.found) throw new DataException();  // Must have closing quote
			
			bay.add(q2.before); // Copy the quoted text across
			s = q2.after;       // The remaining text is after the closing quote
		}
	}
	
	/** Count how many bytes at the start of d are quotable text characters, or false to count data bytes. */
	private static int count(Data d, boolean text) {
		int i = 0;
		while (i < d.size()) {
			byte y = d.get(i);
			if (text ? !isText(y) : isText(y)) break;
			i++; // Count this character and check the next one
		}
		return i;
	}
	
	/** true if d has more text than data characters. */
	private static boolean moreText(Data d) {
		int text = 0; // The number bytes in d we could encode as text or data
		int data = 0; // The number bytes in d we have to encode as data
		for (int i = 0; i < d.size(); i++) {
			byte y = d.get(i);
			if (isText(y)) text++; // 94 of 255 bytes can be encoded as text, that's 37%
			else           data++;
		}
		return text > data; // Picks true for a single byte of text, false for random bytes of data
	}

	/** true if byte y is a text character " " through "~" but not the double quote character. */
	private static boolean isText(byte y) {
		return (y >= ' ' && y <= '~') && y != '\"'; // Otherwise we'll have to encode y as data
	}
	
	// Strike
	
	/** Turn data into text like "hello--", striking out non-text bytes with hyphens. */
	public static void strike(StringBuffer b, Data d) {
		for (int i = 0; i < d.size(); i++) {
			byte y = d.get(i);                           // Loop for each byte of data y in d
			if (y >= ' ' && y <= '~') b.append((char)y); // If it's " " through "~", include it in the text
			else                      b.append('-');     // Otherwise, show a "-" in its place
		}
	}
}
