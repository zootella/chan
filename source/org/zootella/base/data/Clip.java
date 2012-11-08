package org.zootella.base.data;

/** Put a Clip on some data to remove bytes you're done with from the start. */
public class Clip {
	
	// Object
	
	/** Make a new Clip object that clips out all of the given data. */
	public Clip(Data d) {
		data = d;
	}

	/** The data we have left. */
	public Data data() { return data; }
	private Data data; // Not final because we remove bytes by switching to a new Data object
	
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
	@Deprecated public void keep(int n) { //TODO ideally just use remove
		remove(size() - n); // Remove everything but size bytes
	}

	/** Remove n bytes from the start of the data this Clip object views. */
	public void remove(int n) {
		data = data.after(n);
	}
	
	/** Remove n bytes from the start of this Clip object, and return a new Data object that views them. */
	@Deprecated public Data cut(int n) { //TODO ideally just use remove
		Data start = data.start(n);
		remove(n);
		return start;
	}
}
