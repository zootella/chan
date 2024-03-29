package org.zootella.base.valve;

import java.util.LinkedList;
import java.util.List;

import org.zootella.base.data.Bin;
import org.zootella.base.state.Close;

/** A list of Valve objects that data flows through. */
public class Flow extends Close {

	// Make
	
	/** Make a new list of Valve objects that will take in and or put out data. */
	public Flow(boolean in, boolean out) {
		list = new LinkedList<Valve>();
		if (in) this.in = Bin.medium(); // Make the requested bins
		if (out) this.out = Bin.medium();
	}

	/** Our list of Valve objects, data flows from the first through to the last. */
	public final List<Valve> list;
	/** The first Valve in list. */
	public Valve first() { return list.get(0); }
	/** The last Valve in list. */
	public Valve last() { return list.get(list.size() - 1); }

	/** The list's original source of data, null if our first Valve produces data, call go() after adding. */
	public Bin in;
	/** The list's destination for the data it finishes processing, null if our last Valve consumes data, call go() after taking. */
	public Bin out;

	/** Stop the data flowing through this Flow. */
	@Override public void close() {
		if (already()) return;
		for (Valve valve : list)
			close((Close)valve); // Close each Valve in our list
	}
	
	// Go

	/** Move data down this list. */
	public void move() {
		if (closed()) return;
			
		// Stop each valve that doesn't have a later working on its bins
		for (Valve valve : list)
			valve.stop(); // Throws an exception if one stopped it
		
		// Move data down the list, end to start
		Bin.move(last().out(), out);              // Take from the last in the list
		for (Pair<Valve> pair : Pair.pairs(list)) // Move data down the list, bottom to top
			Bin.move(pair.a.out(), pair.b.in());
		Bin.move(in, first().in());               // Give to the first in the list
		
		// Start each valve that has data and space
		for (Valve valve : list)
			valve.start();
	}

	/** true if this Flow is empty of data. */
	public boolean isEmpty() {
		if (in != null && in.hasData())   return false; // Not empty
		for (Valve valve: list)
			if (!valve.isEmpty())         return false;
		if (out != null && out.hasData()) return false;
		return true;
	}
}
