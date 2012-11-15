package org.zootella.base.math;

public class Maximum {
	
	/** True if we have a valid maximum value, false before you add any values. */
	public boolean has() { return has; }
	private boolean has;

	/** The maximum value we've been given, or 0 if has() is false. */
	public long maximum() { return maximum; }
	private long maximum;

	/** Show this object a value to have it keep the largest one. */
	public void add(long value) {
		if (!has | value > maximum) maximum = value;
		has = true;
	}
}
