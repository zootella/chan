package org.zootella.base.data;

/** Clip around some data to remove bytes you're done with from the start until it's empty. */
public class Clip {
	
	// Object
	
	/** Clip this new object around all of the given Data. */
	public Clip(Data d) {
		data = d;
	}

	/** The data we have left. */
	public Data data() { return data; }
	private Data data; // Not final because we remove bytes by switching to a new Data object
	
	/** Make a copy of this Clip object so you can change it without changing this one. */
	public Clip copy() {
		return new Clip(data);
	}
	
	// Size

	/** true if this Clip object is empty, it has a size of 0 bytes. */
	public boolean isEmpty() { return !hasData(); }
	/** true if this Clip object views some data, it has a size of 1 or more bytes. */
	public boolean hasData() {
		return data.hasData();
	}
	
	/** The number of bytes of data this Clip object views. */
	public int size() {
		return data.size();
	}

	// Change

	/** Remove data from the start of this Clip object, keeping only the last n bytes. */
	public void keep(int n) { //TODO ideally just use remove
		remove(size() - n); // Remove everything but size bytes
	}

	/** Remove n bytes from the start of the data this Clip object views. */
	public void remove(int n) {
		data = data.after(n);
	}
	
	/** Remove n bytes from the start of this Clip object, and return a new Data object that views them. */
	public Data cut(int n) { //TODO ideally just use remove
		Data start = data.start(n);
		remove(n);
		return start;
	}
}
