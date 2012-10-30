package org.zootella.base.list;

import java.util.ArrayList;
import java.util.List;

import org.zootella.base.data.Bin;

/** A list of Bin objects ready for quick reuse without memory allocation. */
public class RecycleBin {

	/** Make a recycling bin of Bin objects to keep and reuse them quickly. */
	public RecycleBin(int kind) {
		this.kind = kind;
		list = new ArrayList<Bin>();
	}
	
	/** The size of the bins we hold, like Bin.medium or Bin.big. */
	private final int kind;
	/** Our internal List of Bin objects. */
	private final List<Bin> list;

	/** Finished with bin, add it to this recycling bin. */
	public synchronized void add(Bin bin) { // Synchronized so the event thread and Task threads can recycle bins
		if (bin.capacity() != kind) throw new IllegalArgumentException(); // Check the size
		if (list.size() > capacity)
			return; // We're full
		bin.clear(); // Clear the given bin and keep it
		list.add(bin);
	}

	/** Quickly get a Bin from this recycling bin, or return null if we're empty. */
	public synchronized Bin get() { // Synchronized so the event thread and Task threads can get bins from recycling
		if (list.isEmpty())
			return null; // Fresh out, allocate a new one
		return list.remove(0); // Return one from our list
	}

	/** Maximum number of bins we can hold, 16 Bin.big 64 KB bins take up 1 MB of memory. */
	public static final int capacity = 16;
}
