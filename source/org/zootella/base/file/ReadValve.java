package org.zootella.base.file;

import org.zootella.base.data.Bin;
import org.zootella.base.size.Meter;
import org.zootella.base.size.Range;
import org.zootella.base.state.Close;
import org.zootella.base.valve.Valve;

public class ReadValve extends Close implements Valve {
	
	// Make

	/** Make a ReadValve that will read stripe from file and put data in out(). */
	public ReadValve(File file, Range range) {
		this.file = file;
		meter = new Meter(range);
		out = Bin.medium();
	}
	
	/** The File we read from. */
	private final File file;
	/** Our current task, null if we don't have one right now. */
	private ReadTask task;

	/** Close this Valve so it gives up all resources and won't start again. */
	@Override public void close() {
		if (already()) return;
		if (task != null) {
			close(task);
			task = null; // Discard the closed later so in() and out() work
		}
	}
	
	// Use
	
	public Bin in() { return null; }
	
	public Bin out() {
		if (task != null) return null; // later's worker thread is using our bin, keep it private
		return out;
	}
	private Bin out;
	
	public Meter meter() { return meter; }
	private final Meter meter;
	
	public void start() {
		if (closed()) return;
		if (!meter.isDone() && task == null && out.hasSpace())
			task = new ReadTask(file, meter.remain(), out);
	}
	
	public void stop() {
		if (closed()) return;
		if (task != null && task.closed()) { // Our later finished
			meter.add(task.result().stripe.size); // If an exception closed later, throw it
			task = null; // Discard the closed later, now in() and out() will work
		}
		if (meter.isDone()) close(this); // All done
	}
	
	public boolean isEmpty() {
		return
			task == null && // No later using our bins
			out.isEmpty() && // No data
			meter.isEmpty(); // No responsibility to do more
	}
}
