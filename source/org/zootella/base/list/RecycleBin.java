package org.zootella.base.list;

import java.util.ArrayList;
import java.util.List;

import org.zootella.base.data.Bin;

/** A list of Bin objects ready for quick reuse without memory allocation. */
public class RecycleBin {

	/** Make a recycling bin of Bin objects to keep and reuse them quickly. */
	public RecycleBin() {
		list = new ArrayList<Bin>();
	}
	
	/** Empty Bin objects to use again. */
	private final List<Bin> list;

	/** Finished with bin, add it to this recycle bin. */
	public synchronized void add(Bin bin) {  // Synchronized so the event thread and Task threads can recycle bins here
		if (list.size() >= capacity) return; // We're full, don't keep it
		bin.clear();                         // Clear the given bin and keep it
		list.add(bin);
	}

	/** Quickly get a Bin from this recycle bin, or return null if we're empty. */
	public synchronized Bin get() {      // Synchronized so the event thread and Task threads can get bins from us
		if (list.isEmpty()) return null; // Fresh out, have the caller allocate a new one
		return list.remove(0);           // Return one from our list
	}

	/** Maximum number of bins we can hold, 8 Bin.big 64 KB bins take up 512 KB of memory. */
	public static final int capacity = 8;
}
