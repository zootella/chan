package org.zootella.base.encrypt.pair;

import org.zootella.base.data.Data;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.state.Close;
import org.zootella.base.state.Task;
import org.zootella.base.state.TaskBody;

public class EncryptTask extends Close {

	public EncryptTask(Data data, PairKey key) {
		this.data = data;
		this.key = key;
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	private final Data data;
	private final PairKey key;
	private final Task task;

	@Override public void close() {
		if (already()) return;
		close(task);
	}

	public Data result() { check(exception, encrypted); return encrypted; }
	private ProgramException exception;
	private Data encrypted;
	
	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Data taskEncrypted; // References thread() can safely set

		// A separate thread will call this method
		public void thread() {
			
			taskEncrypted = Pair.encrypt(data, key);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(ProgramException e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			encrypted = taskEncrypted;
			close(EncryptTask.this); // We're done
		}
	}
}
