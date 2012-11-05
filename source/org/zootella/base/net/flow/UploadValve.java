package org.zootella.base.net.flow;

import org.zootella.base.data.Bin;
import org.zootella.base.net.socket.Socket;
import org.zootella.base.size.Meter;
import org.zootella.base.size.Range;
import org.zootella.base.state.Close;
import org.zootella.base.valve.Valve;

public class UploadValve extends Close implements Valve {
	
	// Make

	/** Make an UploadValve that will upload data into socket. */
	public UploadValve(Socket socket, Range range) {
		this.socket = socket;
		meter = new Meter(range);
		in = Bin.medium();
	}
	
	/** The socket we upload to. */
	private final Socket socket;
	/** Our current UploadTask that uploads data from in to socket, null if we don't have one right now. */
	private UploadTask task;

	/** Close this Valve so it gives up all resources and won't start again. */
	public void close() {
		if (already()) return;
		close(task);
		task = null; // Discard the closed later so in() and out() work
	}
	
	// Use

	public Bin in() {
		if (is(task)) return null; // later's worker thread is using our bin, keep it private
		return in;
	}
	private Bin in;
	
	public Bin out() { return null; }
	
	public Meter meter() { return meter; }
	private final Meter meter;

	public void start() {
		if (closed()) return;
		if (!meter.isDone() && no(task) && in.hasData())
			task = new UploadTask(socket, meter.remain(), in);
	}
	
	public void stop() {
		if (closed()) return;
		if (done(task)) {
			meter.add(task.result().size); // If an exception closed later, throw it
			task = null; // Discard the closed later, now in() and out() will work
		}
		if (meter.isDone()) close(this); // All done
	}
	
	public boolean isEmpty() {
		return
			no(task)      && // No later using our bins
			in.isEmpty()  && // No data
			meter.isEmpty(); // No responsibility to do more
	}
}
