package org.zootella.base.time;

import org.zootella.base.exception.TimeException;

/** Make and check an Egg timer to close with a TimeException when the disk or network made you wait for 4 seconds. */
public class Egg {
	
	public Egg() { this(Time.out); }
	public Egg(long delay) {
		start = new Now();
		this.delay = delay;
	}
	
	public final Now start;
	public final long delay;

	public void check() {
		if (start.expired(delay)) throw new TimeException();
	}
}
