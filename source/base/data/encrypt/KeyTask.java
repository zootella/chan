package base.data.encrypt;

import base.data.Data;
import base.exception.ProgramException;
import base.state.Close;
import base.state.Task;
import base.state.TaskBody;
import base.state.Update;

public class KeyTask extends Close {

	public KeyTask(Update up) {
		this.up = up; // We'll tell update when we're done
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	private final Update up;
	private final Task task;

	@Override public void close() {
		if (already()) return;
		close(task);
		up.send();
	}

	/** How much we hashed when we're done, or throws the exception that made us give up. */
	public Data result() { check(exception, data); return data; }
	private ProgramException exception;
	private Data data;
	
	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Data taskData; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {

			taskData = Encrypt.key();
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(ProgramException e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			data = taskData;
			close(me());          // We're done
		}
	}
	private KeyTask me() { return this; } // Give inner code a link to the outer object
}
