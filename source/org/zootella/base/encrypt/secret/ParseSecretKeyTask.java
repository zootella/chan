package org.zootella.base.encrypt.secret;

import org.zootella.base.data.Data;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.state.Close;
import org.zootella.base.state.Task;
import org.zootella.base.state.TaskBody;

public class ParseSecretKeyTask extends Close {

	public ParseSecretKeyTask(Data data) {
		this.data = data;
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	private final Data data;
	private final Task task;

	@Override public void close() {
		if (already()) return;
		close(task);
	}

	public KeySecret result() { check(exception, key); return key; }
	private ProgramException exception;
	private KeySecret key;
	
	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private KeySecret taskKey; // References thread() can safely set

		// A separate thread will call this method
		public void thread() {

			taskKey = Secret.parse(data);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(ProgramException e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			key = taskKey;
			close(ParseSecretKeyTask.this); // We're done
		}
	}
}
