package org.zootella.base.net.upnp.task;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.net.upnp.Access;
import org.zootella.base.net.upnp.Do;
import org.zootella.base.net.upnp.name.Map;
import org.zootella.base.state.OldClose;
import org.zootella.base.state.Result;
import org.zootella.base.state.Task;
import org.zootella.base.state.TaskBody;
import org.zootella.base.state.Update;

public class RemoveTask extends OldClose {
	
	// Make

	public RemoveTask(Update up, Access router, Map forward) {
		this.up = up; // We'll tell above when we're done
		this.router = router;
		this.forward = forward;
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	private final Update up;
	private final Access router;
	private final Map forward;
	private final Task task;

	@Override public void close() {
		if (already()) return;
		close(task);
		up.send();
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

			taskResult = Do.remove(router, forward);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(ProgramException e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			result = taskResult;
			close(RemoveTask.this); // We're done
		}
	}
}
