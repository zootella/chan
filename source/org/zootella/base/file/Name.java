package org.zootella.base.file;

import org.zootella.base.data.Data;
import org.zootella.base.data.Text;
import org.zootella.base.data.TextSplit;

/** A file name and extension, like "name.ext". */
public class Name implements Comparable<Name> {
	
	// Make
	
	/** Make a new blank Name. */
	public Name() {
		name = "";
		extension = "";
	}

	/** Make a new Name from "name" and "ext", without a period, trims both. */
	public Name(String name, String extension) {
		this.name = name.trim();
		this.extension = extension.trim();
	}
	
	/** Parse a String like "name.ext" into a Name object. */
	public Name(String s) {
		TextSplit split = Text.splitLast(s, "."); // Split around the last "." to separate the file name from the extension
		name = split.before.trim(); // Remove any space from their edges
		extension = split.after.trim();
	}
	
	// Look
	
	/** The file name, like "name". */
	public final String name;
	/** The file name extension, like "ext", without a period. */
	public final String extension;

	/** Turn this Name into a String like "name.ext". */
	public String toString() {
		String s = name;
		if (Text.is(extension)) s += "." + extension; // Only add the period if we have an extension
		return s;
	}
	
	/** true if this Name has text, toString() won't give you "". */
	public boolean is() { return Text.is(toString()); }
	/** true if this Name is blank, toString() will give you "". */
	public boolean isBlank() { return Text.isBlank(toString()); }
	
	// Compare
	
	@Override public int compareTo(Name o) {
		return toString().compareTo(o.toString());
	}

	@Override public boolean equals(Object o) {
		if (o == null || !(o instanceof Name)) return false;
		return name.equals(((Name)o).name) && extension.equals(((Name)o).extension);
	}
	
	@Override public int hashCode() {
		return name.hashCode() * extension.hashCode();
	}

	// Base
	
	//TODO add same() which matches case for windows, "Name.ext" and "name.ext" can be in the same folder on linux, but not windows
	
	/** Return a new Name that is this one with safe characters, and turn blank into "Index". */
	public Name safe() {
		Name n = new Name(safe(name), safe(extension));
		if (n.isBlank()) n = new Name("Index"); // Return "Index" instead of a blank Name
		return n;
	}
	
	/** Replace characters that aren't allowed in a file name on the disk with characters that are. */
	private static String safe(String s) {
		s = Text.replace(s, "\\", "-"); // Replace \ / : * ? < > | with -
		s = Text.replace(s,  "/", "-");
		s = Text.replace(s,  ":", "-");
		s = Text.replace(s,  "*", "-");
		s = Text.replace(s,  "?", "-");
		s = Text.replace(s,  "<", "-");
		s = Text.replace(s,  ">", "-");
		s = Text.replace(s,  "|", "-");
		s = Text.replace(s, "\"", "'"); // Replace " with '
		return s;
	}
	
	/** Make a new Name that is this one with a number, like "name (2).ext", doesn't say 0 or 1. */
	public Name number(int number) {
		if (number < 2) return this; // No change necessary
		return new Name(name + " (" + number + ")", extension);
	}

	/** Make a new Name that is this one with the extension changed to lower case. */
	public Name lower() {
		return new Name(name, extension.toLowerCase());
	}

	/** Make a new Name like "ryio3tz5.db" that won't conflict with files already in a folder. */
	public static Name unique() {
		return new Name(Text.start(Data.random(8).base32(), 8), "db");
	}
}
