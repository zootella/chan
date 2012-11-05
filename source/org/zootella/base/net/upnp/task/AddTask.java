package org.zootella.base.net.upnp.task;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.net.upnp.Access;
import org.zootella.base.net.upnp.Do;
import org.zootella.base.net.upnp.name.Map;
import org.zootella.base.state.Close;
import org.zootella.base.state.Result;
import org.zootella.base.state.Task;
import org.zootella.base.state.TaskBody;

public class AddTask extends Close {
	
	// Make

	public AddTask(Access router, Map forward) {
		this.router = router;
		this.forward = forward;
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	private final Access router;
	private final Map forward;
	private final Task task;

	@Override public void close() {
		if (already()) return;
		close(task);
	}

	// Result
	
	public Result<Map> result() { check(exception, result); result.check(); return result; }
	private ProgramException exception;
	private Result<Map> result;
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Result<Map> taskResult; // References thread() can safely set

		// A separate thread will call this method
		public void thread() {
			
			taskResult = Do.add(router, forward);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(ProgramException e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			result = taskResult;
			close(AddTask.this); // We're done
		}
	}
}
