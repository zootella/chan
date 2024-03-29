package org.zootella.base.data;

import java.util.ArrayList;
import java.util.List;

import org.zootella.base.exception.ChopException;

public class Text {

	// Same

	/** Compare two strings, matching cases. */
	public static boolean same(String s1, String s2) {
		if (s1.length() != s2.length()) return false;   // Make sure s1 and s2 are the same length
		else if (s1.length() == 0) return true;         // Blanks are the same
		return search(s1, s2, true, false, true) != -1; // Search at the start only
	}
	/** Compare two strings, case sensitive. */
	public static boolean sameCase(String s1, String s2) {
		if (s1.length() != s2.length()) return false;    // Make sure s1 and s2 are the same length
		else if (s1.length() == 0) return true;          // Blanks are the same
		return search(s1, s2, true, false, false) != -1; // Search at the start only
	}

	// Starts, ends, and has

	/** Determine if a String starts with a tag, matching cases. */
	public static boolean starts(String s, String tag) { return search(s, tag, true, false, true) != -1; }
	/** Determine if a String starts with a tag, case sensitive. */
	public static boolean startsCase(String s, String tag) { return search(s, tag, true, false, false) != -1; }

	/** Determine if a String ends with a tag, matching cases. */
	public static boolean ends(String s, String tag) { return search(s, tag, false, false, true) != -1; }
	/** Determine if a String ends with a tag, case sensitive. */
	public static boolean endsCase(String s, String tag) { return search(s, tag, false, false, false) != -1; }

	/** Determine if a String contains a tag, matching cases. */
	public static boolean has(String s, String tag) { return search(s, tag, true, true, true) != -1; }
	/** Determine if a String contains a tag, case sensitive. */
	public static boolean hasCase(String s, String tag) { return search(s, tag, true, true, false) != -1; }

	// Find
	
	/** Find where tag1 or tag2 first appears in s, -1 if neither found. */
	public static int find(String s, String tag1, String tag2) {
		int i1 = find(s, tag1); // Search for both
		int i2 = find(s, tag2);
		if (i1 == -1 && i2 == -1) return -1; // Both not found
		else if (i1 == -1) return i2; // One found, but not the other
		else if (i2 == -1) return i1;
		else return Math.min(i1, i2); // Both found, return the one that appears first
	}

	/** Find the character index in a String where a tag appears, matching cases, -1 if not found. */
	public static int find(String s, String tag) { return search(s, tag, true, true, true); }
	/** Find the character index in a String where a tag appears, case sensitive, -1 if not found. */
	public static int findCase(String s, String tag) { return search(s, tag, true, true, false); }

	/** Find the character index in a String where a tag last appears, matching cases, -1 if not found. */
	public static int last(String s, String tag) { return search(s, tag, false, true, true); }
	/** Find the character index in a String where a tag last appears, case sensitive, -1 if not found. */
	public static int lastCase(String s, String tag) { return search(s, tag, false, true, false); }

	/**
	 * Find where in a String a tag appears.
	 * 
	 * @param s       The String to search.
	 * @param tag     The tag to look for.
	 * @param forward true to search forwards from the start.
	 *                false to search backwards from the end.
	 * @param scan    true to scan across all the positions possible in s.
	 *                false to only look at the starting position.
	 * @return        The character index in s where the tag starts.
	 *                -1 if not found.
	 */
	private static int search(String s, String tag, boolean forward, boolean scan, boolean match) {

		// Get and check the lengths
		int sLength = s.length(); // Measured in number of characters, not bytes
		int tagLength = tag.length();
		if (tagLength == 0) throw new IllegalArgumentException();
		if (sLength < tagLength) return -1;

		// Our search will scan s from the start index through the end index
		int start = forward ? 0                   : sLength - tagLength;
		int end   = forward ? sLength - tagLength : 0;
		int step  = forward ? 1                   : -1;

		// If we're not allowed to scan across the text, set end to only look one place
		if (!scan) end = start;

		// Scan sIndex from the start through the end in the specified direction
		for (int sIndex = start; sIndex != end + step; sIndex += step) {

			// Look for the tag at sIndex
			int tagIndex;
			for (tagIndex = 0; tagIndex < tagLength; tagIndex++) {

				// Get the characters to compare
				char sCharacter = s.charAt(sIndex + tagIndex);
				char tagCharacter = tag.charAt(tagIndex);

				// The caller requested matching cases
				if (match) {

					// Change both characters to lower case so they match
					sCharacter = Character.toLowerCase(sCharacter);
					tagCharacter = Character.toLowerCase(tagCharacter);
				}

				// Mismatch found, break to move to the next spot in s
				if (sCharacter != tagCharacter) break;
			}

			// We found the tag at sIndex, return it
			if (tagIndex == tagLength) return sIndex;
		}

		// Not found
		return -1;
	}
	
	// Blank or not
	
	/** true if s is null or "", blank. */
	public static boolean isBlank(String s) {
		return s == null || s.equals("");
	}

	/** true if s has text, false if it's null or "", blank. */
	public static boolean is(String s) {
		return !isBlank(s);
	}

	// Split, clip and replace
	
	/** Clip the part of s that is before a tag, returns s if not found. */
	public static String before(String s, String tag) {
		Split<String> split = split(s, tag);
		return split.before;
	}
	
	/** Clip the part of s that is after a tag, returns "" if not found. */
	public static String after(String s, String tag) {
		Split<String> split = split(s, tag);
		return split.after;
	}

	/** Clip the part of s that is before the last place a tag appears, returns s if not found. */
	public static String beforeLast(String s, String tag) {
		Split<String> split = splitLast(s, tag);
		return split.before;
	}
	
	/** Clip the part of s that is after the last place a tag appears, returns "" if not found. */
	public static String afterLast(String s, String tag) {
		Split<String> split = splitLast(s, tag);
		return split.after;
	}

	/** Split s around tag to get what's before and after. */
	public static Split<String> split(String s, String tag) { return split(s, tag, true, true); }
	/** Split s around tag to get what's before and after, matching cases. */
	public static Split<String> splitCase(String s, String tag) { return split(s, tag, true, false); }
	/** Split s around where a tag last appears to get what's before and after. */ 
	public static Split<String> splitLast(String s, String tag) { return split(s, tag, false, true); }
	/** Split s around where a tag last appears to get what's before and after, matching cases. */
	public static Split<String> splitLastCase(String s, String tag) { return split(s, tag, false, false); }

	/**
	 * Split a String around a tag, finding the parts before and after it.
	 * 
	 * @param s       The String to search.
	 * @param tag     The tag to look for.
	 * @param forward true to find the first place the tag appears.
	 *                false to search backwards from the end.
	 * @param match   true to match upper and lower case characters.
	 *                false to be case-sensitive.
	 * @return        A TextSplit object that tells if the tag was found, and clips out the strings before and after it.
	 *                If the tag is not found, textSplit.before will be s, and textSplit.after will be blank.
	 */
	private static Split<String> split(String s, String tag, boolean forward, boolean match) {

		int i = search(s, tag, forward, true, match); // Search s for the tag
		if (i == -1)
			return new Split<String>(false, s, "", ""); // Not found, make before s and after blank
		else
			return new Split<String>(true, start(s, i), tag, after(s, i + tag.length())); // We found the tag at i, clip out the text before and after it
	}

	/** Clip out size characters starting at i in the given String s, clip(s, 5, 3) is cccccCCCcc. */
	public static String clip(String s, int i, int size) { return s.substring(i, i + size); }
	/** Clip out the first size characters in the given String s, start(s, 3) is CCCccccccc. */
	public static String start(String s, int size) { return s.substring(0, size); }
	/** Clip out the last size characters in the given String s, end(s, 3) is cccccccCCC. */
	public static String end(String s, int size) { return s.substring(s.length() - size, s.length()); }
	/** Clip out the end of the given String s from index i to the end, after(s, 3) is cccCCCCCCC. */
	public static String after(String s, int i) { return s.substring(i); } // Java's String has a method that does this
	/** Chop the last size characters off the end of a given String, returning the start before them, chop(s, 3) is CCCCCCCccc. */
	public static String chop(String s, int size) { return s.substring(0, s.length() - size); }
	
	/** Split s like "line1 \n line2 \n line3" into a List of 3 strings. */
	public static List<String> lines(String s) { return words(s, "\n"); } // Splits around "\n" and trims "\r"
	/** Split s like "word1 word2 word3" into a List of 3 strings. */
	public static List<String> words(String s) { return words(s, " "); }

	/**
	 * Split s around all the instances of a tag.
	 * For instance, words("a:b:c", ":") returns a List of 3 String objects, "a", "b", and "c".
	 * Trims whitespace characters from the strings in the List, and doesn't include blank strings in the List.
	 * If the tag is not found, returns a List with one String, s.
	 */
	public static List<String> words(String s, String tag) {

		// Make a new empty List of String objects for us to fill and return
		List<String> list = new ArrayList<String>();

		// Loop until s is blank
		while (is(s)) {
			Split<String> split = Text.split(s, tag); // Split s around the first instance of the tag in it
			String word = split.before.trim();        // Trim spaces from around the word we found before the tag, and save it
			s = split.after;                          // Next time, we'll split the part that came after
			if (is(word)) list.add(word);             // If the word isn't blank, add it to the List we're making
		}
		return list;
	}

	/**
	 * Replace all the tags in s with something else.
	 * For instance, replace("a-b-c", "-", "_") is "a_b_c".
	 */
	public static String replace(String s, String tag, String replace) {

		// Make a StringBuffer to fill with text as we break off parts and make the replacement
		StringBuffer done = new StringBuffer();
		
		// Loop until s is blank
		while (is(s)) {
			Split<String> split = split(s, tag);   // Split s around the first instance of the tag in it
			done.append(split.before);             // Move the part before from s to done
			if (split.found) done.append(replace);
			s = split.after;
		}
		return done.toString();
	}

	// Trim

	/**
	 * Remove all the instances of any number of tags from the start and end of a String.
	 * For instance, trim("abc hello abc", "a", "b", "c") returns "hello".
	 * Matches cases.
	 * Trims whitespace automatically, you don't have to include " " in the tags.
	 */
	public static String trim(String s, String... tags) {

		// Loop until a pass doesn't change s
		while (true) {
			boolean changed = false;

			// Remove whitespace from the start and end
			String trimmed = s.trim();
			if (!s.equals(trimmed)) {
				s = trimmed;
				changed = true;
			}
			
			// Loop for each tag we need to remove
			for (String tag : tags) {

				// If s starts with this tag, remove it
				if (starts(s, tag)) {
					s = after(s, tag.length());
					changed = true;
				}

				// If s ends with this tag, remove it
				if (ends(s, tag)) {
					s = chop(s, tag.length());
					changed = true;
				}
			}

			// If we found nothing and didn't change s, we're done
			if (!changed) return s;
		}
	}

	// Parse

	/**
	 * Remove a group of lines of text from the start of c, and parse them into a List of String objects.
	 * Lines end "\n" or "\r\n", and a blank line marks the end of the group.
	 * Removes the terminating blank line from c, but doesn't include it in the return List.
	 * If c doesn't have a blank line, throws a ChopException without changing c.
	 */
	public static List<String> group(Clip c) {
		Clip clip = c.copy(); // Make a copy to throw an exception with c unchanged
		List<String> list = new ArrayList<String>();
		while (true) {
			String line = line(clip); // Parse a line from data
			if (Text.isBlank(line)) break; // We got the blank line that ends the group, done
			list.add(line); // We got a line, add it to the list we'll return
		}
		c.keep(clip.size()); // That worked without an exception, remove the data we parsed from c
		return list;
	}

	/**
	 * Remove one line of text from the start of c, and parse it into a String.
	 * If c doesn't have a "\n", throws a ChopException and doesn't change c.
	 * Works with lines that end with both "\r\n" and just "\n", removes both without trimming the String.
	 */
	public static String line(Clip c) {
		Split<Data> split = c.data().split((byte)'\n'); // The line ends "\r\n" or just "\n", split around "\n"
		if (!split.found) throw new ChopException(); // A whole line hasn't arrived yet
		Data before = split.before;
		if (before.ends((byte)'\r')) before = before.chop(1); // Remove the "\r"
		c.keep(split.after.size()); // That all worked, remove the data of the line from c
		return before.toString();
	}
	
	// Character

	/** true if c is a letter 'a' through 'z' or 'A' through 'Z'. */
	public static boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	/** true if c is a digit '0' through '9'. */
	public static boolean isNumber(char c) {
		return (c >= '0' && c <= '9');
	}
	
	// Quote
	
	/** Replace | with " to make string literals look nicer. */
	public static String quote(String s) {
		return replace(s, "|", "\"");
	}
	
	
	
	

	/** Add all the given strings together. */
	public static String add(String... strings) {
		StringBuffer b = new StringBuffer();
		for (String s : strings)
			b.append(s);
		return b.toString();
	}

	/** Add all the given strings together, and put a "\r\n" on the end. */
	public static String line(String... strings) {
		StringBuffer b = new StringBuffer();
		for (String s : strings)
			b.append(s);
		b.append("\r\n"); // End the line
		return b.toString();
	}
	
	/** Format the given list of strings into a fixed width text table with the given number of columns. */
	public static String table(int columns, String... cells) {
		
		int[] widths = new int[columns]; // Loop to determine how wide each column needs to be
		for (int i = 0; i < cells.length; i++) {
			if (widths[i % columns] < cells[i].length())
				widths[i % columns] = cells[i].length();
		}
		
		StringBuffer b = new StringBuffer(); // Loop for each cell to assemble the table
		for (int i = 0; i < cells.length; i++) {
			String s = cells[i];

			if (i % columns != columns - 1) {            // Before the last column
				while (s.length() < widths[i % columns]) // Make this cell wide enough for the column it's in
					s += " ";
				b.append(s + "  ");
			} else {                                     // Last column
				b.append(s + "\r\n");
			}
		}
		return b.toString();
	}






}
