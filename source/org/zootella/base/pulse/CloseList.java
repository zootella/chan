package org.zootella.base.pulse;

import java.util.ArrayList;
import java.util.List;

import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;

/** The program's list of objects that extend Close. */
public class CloseList {
	
	/** Our internal list to hold the objects. */
	private final List<Close> list = new ArrayList<Close>();

	/**
	 * How many objects are in the list right now.
	 * Keep in mind that a Task thread might add another object right after you call this.
	 */
	public synchronized int size() {
		return list.size();
	}

	/** Get the Close object at index i in our list, or null if that's beyond the edge right now. */
	public synchronized Close get(int i) {
		try {
			return list.get(i);
		} catch (IndexOutOfBoundsException e) { return null; } // Only the event thread calls size() and get(), so shouldn't happen
	}
	
	/** The program just made a new object that extends Close, add it to the program's list. */
	public synchronized void add(Close c) { // If a Task thread creates a new Close object, it will enter this method
		list.add(c); // The objects in the list are in the order they were made so contained objects are after those that made them
	}

	/** Remove the object at index i from the list. */
	public synchronized void remove(int i) {
		try {
			list.remove(i);
		} catch (IndexOutOfBoundsException e) { Mistake.ignore(e); } // Only the event thread calls size() and remove(), so shouldn't happen
	}
}
