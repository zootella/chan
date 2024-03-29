package org.zootella.base.encrypt.hash;

import org.zootella.base.data.Bin;
import org.zootella.base.size.Meter;
import org.zootella.base.size.Range;
import org.zootella.base.state.Close;
import org.zootella.base.valve.Valve;

public class HashValve extends Close implements Valve {
	
	// Make

	/** Make a HashValve that will take data from in() and hash it. */
	public HashValve(Range range) {
		hash = new Hash();
		meter = new Meter(range);
		in = Bin.medium();
	}
	
	/** The Hash that hashes the data. */
	public final Hash hash;
	/** Our current task, null if we don't have one right now. */
	private HashTask task;

	/** Close this Valve so it gives up all resources and won't start again. */
	@Override public void close() {
		if (already()) return;
		if (task != null) {
			close(task);
			task = null; // Discard the closed task so in() and out() work
		}
		close(hash);
	}

	// Valve
	
	public Bin in() {
		if (task != null) return null; // later's worker thread is using our bin, keep it private
		return in;
	}
	private Bin in;
	
	public Bin out() { return null; }
	
	public Meter meter() { return meter; }
	private final Meter meter;
	
	public void start() {
		if (closed()) return;
		if (!meter.isDone() && task == null && in.hasData())
			task = new HashTask(hash, in, meter.remain());
	}

	public void stop() {
		if (closed()) return;
		if (task != null && task.closed()) { // Our later finished
			meter.add(task.result().size); // If an exception closed later, result() will throw it
			task = null; // Discard the closed later, now in() and out() will work
		}
		if (meter.isDone()) close(this); // All done
	}
	
	public boolean isEmpty() {
		return
			task == null && // No later using our bins
			in.isEmpty()  && // No data
			meter.isEmpty(); // No responsibility to do more
	}
}
