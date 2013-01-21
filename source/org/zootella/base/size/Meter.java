package org.zootella.base.size;

import org.zootella.base.time.OldSpeed;

/** A Meter has done that grows in a Range. */
public class Meter {
	
	// Make

	/** Make a Meter with a Range that starts at 0 and imposes no Limit. */
	public Meter() { this(Range.unlimited()); }

	/** Make a Meter with the given Range. */
	public Meter(Range range) {
		this.range = range;
		speed = new OldSpeed();
	}

	// Look

	/** The whole Range this Meter is going over. */
	private final Range range;
	
	/** How much this Meter has done in its range. */
	public long done() { return done; }
	private long done;

	/** The Range that remains for us to do. */
	public Range remain() { return range.after(done); }
	/** true if this Meter must not do more. */
	public boolean isDone() { return remain().isDone(); }
	/** true if this Meter is empty of responsibility to do more. */
	public boolean isEmpty() { return remain().isEmpty(); }

	/** How fast done() is growing. */
	public final OldSpeed speed;
	
	// Add
	
	/** Record that we've done the given distance more. */
	public void add(long more) {
		if (more < 1) throw new IndexOutOfBoundsException();
		range.check(done + more);
		done += more;
	}
	
	
	
	public Range range() { return range; }
}
