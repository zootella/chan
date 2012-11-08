package org.zootella.base.list;

import java.util.HashSet;
import java.util.Set;

import org.zootella.base.state.Close;
import org.zootella.base.time.Now;

public class TwoBoots<Type> extends Close {
	
	// Make

	/**
	 * Make a new TwoBoots<Type>() to hold objects of type Type.
	 * It will keep an object you add for at least delay milliseconds but not twice that long.
	 * If the objects inside extend Close, we'll call close() on them before throwing them out.
	 */
	public TwoBoots(long delay) {
		this.delay = delay;
		age = new Now();
		current = new HashSet<Type>();
		previous = new HashSet<Type>();
	}
	
	private final long delay;   // Every delay milliseconds, we cycle the boots
	private Now age;            // When we last cycled the boots
	private Set<Type> current;  // The current boot we add objects to
	private Set<Type> previous; // The previous boot we just keep around

	@Override public void close() {
		if (already()) return;
		closeContents(current);
		closeContents(previous);
	}
	
	// Keep

	/** Add t to this TwoBoots, we'll keep it for awhile, then close and discard it. */
	public void add(Type t) {
		confirmOpen();
		cycle();
		if (!current.contains(t) && !previous.contains(t))
			current.add(t);
	}

	/** Remove t from this TwoBoots, does not close it. */
	public void remove(Type t) {
		confirmOpen();
		cycle();
		current.remove(t);
		previous.remove(t);
	}

	/** All the objects currently in this TwoBoots. */
	public Set<Type> list() {
		confirmOpen();
		cycle();
		Set<Type> set = new HashSet<Type>();
		set.addAll(current);
		set.addAll(previous);
		return set;
	}
	
	// Help

	/** If it's been long enough since the last time, close and remove the oldest objects we carry. */
	private void cycle() {
		if (age.expired(delay)) {
			age = new Now();
			closeContents(previous);
			previous = current;
			current = new HashSet<Type>();
		}//TODO and if it's doubly expired, you need to toss out *both* boots, right?
	}
	
	/** If we're holding objects that extend Close, call close() on all of them in set. */
	private void closeContents(Set<Type> set) {
		for (Type t : set)
			if (t instanceof Close)
				close((Close)t);
	}
}
