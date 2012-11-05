package org.zootella.base.encrypt.sign;

import org.zootella.base.data.Data;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.state.Close;
import org.zootella.base.state.Task;
import org.zootella.base.state.TaskBody;

public class SignTask extends Close {

	public SignTask(Data data, SignKey key) {
		this.data = data;
		this.key = key;
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	private final Data data;
	private final SignKey key;
	private final Task task;

	@Override public void close() {
		if (already()) return;
		close(task);
	}

	public Data result() { check(exception, signature); return signature; }
	private ProgramException exception;
	private Data signature;
	
	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Data taskSignature; // References thread() can safely set

		// A separate thread will call this method
		public void thread() {
			
			taskSignature = Sign.sign(data, key);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(ProgramException e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			signature = taskSignature;
			close(SignTask.this); // We're done
		}
	}
}
