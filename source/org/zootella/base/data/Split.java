package org.zootella.base.data;

/** A Split object holds the results of looking for a tag in text or data. */
public class Split<Type> {
	
	/** Package the result of a split search. */
	public Split(boolean found, Type before, Type tag, Type after) {
		this.found = found;
		this.before = before;
		this.tag = tag;
		this.after = after;
	}

	/** true if we found the tag. */
	public final boolean found;
	
	/**
	 * The text or data that is before the tag.
	 * If the tag was not found, before is all the text or data.
	 */
	public final Type before;
	
	/**
	 * The tag in the text or data.
	 * If the tag was not found, tag is blank or empty.
	 */
	public final Type tag;

	/**
	 * The data that is after the tag.
	 * If the tag was not found, after is blank or empty.
	 */
	public final Type after;
}
