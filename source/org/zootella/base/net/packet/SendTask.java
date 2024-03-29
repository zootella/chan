package org.zootella.base.net.packet;

import org.zootella.base.data.Bin;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.state.Close;
import org.zootella.base.state.Task;
import org.zootella.base.state.TaskBody;

public class SendTask extends Close {

	// Make

	/** Have listen send packet, don't look at packet after this. */
	public SendTask(ListenPacket listen, Packet packet) {
		this.listen = listen;
		this.packet = packet;
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	private final ListenPacket listen;
	private final Packet packet;
	private final Task task;

	@Override public void close() {
		if (already()) return;
		close(task);
	}

	// Result
	
	/** The empty Bin you can reuse, or throws the exception that made us give up. */
	public Bin result() { check(exception, bin); return bin; }
	private ProgramException exception;
	private Bin bin;

	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {

		// A separate thread will call this method
		public void thread() {
				
			// Use listen to send bin's data to ipPort in a UDP packet
			packet.bin.send(listen, packet.ipPort);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(ProgramException e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			bin = packet.bin;
			bin.clear();
			close(SendTask.this); // We're done
		}
	}
}
