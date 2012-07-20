package org.zootella.base.data;

import org.zootella.base.exception.DataException;

public class Quote {

	
	
	
	
	

	

	// Quote

	/** Turn data into base 16 text with bytes that are characters in quotes, abc\r\n becomes "abc"0d0a */
	public static void encode(StringBuffer b, Data d) {

		Data data = d.copy();    // Copy d to remove what we've encoded from data
		while (data.hasData()) { // Stop when data is empty
			byte y = data.first();

			if (text(y)) {

				b.append('\"');
				b.append(data.cut(count(data, true)).toString()); // Surround bytes that are text characters with quotes				
				b.append('\"');

			} else {

				Encode.toBase16(b, data.cut(count(data, false))); // Encode other bytes into base 16 outside the quotes
			}
		}
	}

	/** Count how many bytes at the start of d are quotable text characters, or false to count data bytes. */
	private static int count(Data d, boolean text) {
		int i = 0;
		while (i < d.size()) {
			byte y = d.get(i);
			if (text ? !text(y) : text(y)) break;
			i++;
		}
		return i;
	}

	/** true if byte y is a text character " " through "~" but not the double quote character, false to encode y as data. */
	public static boolean text(byte y) {
		return (y >= ' ' && y <= '~') && y != '\"';
	}
	
	
	
	
	
	//start out on bytes
	// loop i for each character
	
	
	
	//ok, but this doesn't work for "value"0d0a #comment
	
	//confirm you get a data exception on
	// testUnquote("poop",                           "", false);
	// testUnquote("\"value",                        "", false);
	// testUnquote("0a0b\"value",                    "", false);
	// testUnquote("\"hello you\"0d0a poop#comment", "", false);
	// testUnquote("\"hello you\"0d0apoop#comment,   "", false);
	// testUnquote("0\"hello you\"0d0a",             "", false);
	// testUnquote("poop\"hello you\",               "", false);
	
	//confirm these are ok
	// testUnquote("\"hello you\"0d0a",          "hello you\r\n", true);
	// testUnquote("\"hello you\"0d0a#comment",  "hello you\r\n", true);
	// testUnquote("\"hello you\"0d0a #comment", "hello you\r\n", true);
	// testUnquote("\"room #9\"0d0a",          "room #9\r\n", true);
	// testUnquote("\"room #9\"0d0a#comment",  "room #9\r\n", true);
	// testUnquote("\"room #9\"0d0a #comment", "room #9\r\n", true);
	
	public static void decode(Bay bay, String s) {
		
		while (Text.is(s)) {
			
			TextSplit split1 = Text.split(s, "\"");
			TextSplit split3 = Text.split(split1.before, "#");
			
			if (split3.found) {

				bay.add(Encode.fromBase16(split3.before.trim()));
				return;
				
			} else {
				
				bay.add(Encode.fromBase16(split1.before));
			}
			
			if (split1.found) {
				
				TextSplit split2 = Text.split(split1.after, "\"");
				if (!split2.found) throw new DataException();
				
				bay.add(split2.before);
				s = split2.after;
				
			} else {
				
				s = split1.after;
			}
		}
	}
	
	
	
	
	/** Turn quoted text back into the data it was made from. */
	/*
	public static void decode(Bay bay, String s) {
		try {

			// Move i down s, stopping when it reaches the end
			int i = 0;
			while (i != s.length()) {
				char c = s.charAt(i); // Get the character at i

				// i is at "
				if (c == '[') {
					if (s.charAt(i + 1) == '[') {
						bay.add((byte)'['); // Add "[" and move past the "[["
						i += 2;

					// i is at the start of a base 16 encoded block like "[0a0d]"
					} else {
						i++;                                     // Move i past the opening "["
						int j = i;                               // Start j there
						while (s.charAt(j) != ']') j++;          // Move j to the closing "]"
						fromBase16(bay, Text.clip(s, i, j - i)); // Base 16 decode the contents into bay
						i = j + 1;                               // Move i beyond the block
					}

				// i is at "]]"
				} else if (c == ']') {
					if (s.charAt(i + 1) == ']') {
						bay.add((byte)']'); // Add "]" and move past the "]]"
						i += 2;

					// The next character has to be the second "]" in "]]"
					} else {
						throw new DataException();
					}

				// i is at a character like "a"
				} else {
					bay.add((byte)c); // Add it and move past it
					i++;
				}
			}

		// If we didn't have enough characters, like "hello[00", throw a DataException
		} catch (IndexOutOfBoundsException e) { throw new DataException(); }
	}
	*/
	
	
	
	
	
	
	
		
	
	
	
	
	
	
	
	
	
}
