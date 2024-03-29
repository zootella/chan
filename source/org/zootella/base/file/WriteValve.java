package org.zootella.base.file;

import org.zootella.base.data.Bin;
import org.zootella.base.size.Meter;
import org.zootella.base.size.Range;
import org.zootella.base.state.Close;
import org.zootella.base.valve.Valve;

public class WriteValve extends Close implements Valve {
	
	// Make
	
	/** Make a WriteValve that will take data from in() and write it at index in file. */
	public WriteValve(File file, Range range) {
		this.file = file;
		meter = new Meter(range);
		in = Bin.medium();
	}
	
	/** The open File we write to. */
	private final File file;
	/** Our current WriteLater, null if we don't have one right now. */
	private WriteTask later;

	/** Close this Valve so it gives up all resources and won't start again. */
	@Override public void close() {
		if (already()) return;
		if (later != null) {
			close(later);
			later = null; // Discard the closed later so in() and out() work
		}
	}
	
	// Use

	public Bin in() {
		if (later != null) return null; // later's worker thread is using our bin, keep it private
		return in;
	}
	private Bin in;
	
	public Bin out() { return null; }
	
	public Meter meter() { return meter; }
	private final Meter meter;
	
	public void start() {
		if (closed()) return;
		if (!meter.isDone() && later == null && in.hasData())
			later = new WriteTask(file, meter.remain(), in);
	}
	
	public void stop() {
		if (closed()) return;
		if (later != null && later.closed()) { // Our later finished
			meter.add(later.result().stripe.size); // If an exception closed later, throw it
			later = null; // Discard the closed later, now in() and out() will work
		}
		if (meter.isDone()) close(this); // All done
	}
	
	public boolean isEmpty() {
		return
			later == null && // No later using our bins
			in.isEmpty()  && // No data
			meter.isEmpty(); // No responsibility to do more
	}
}
