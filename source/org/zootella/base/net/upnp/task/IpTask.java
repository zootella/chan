package org.zootella.base.net.upnp.task;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.net.name.Ip;
import org.zootella.base.net.upnp.Access;
import org.zootella.base.net.upnp.Do;
import org.zootella.base.state.OldClose;
import org.zootella.base.state.Result;
import org.zootella.base.state.Task;
import org.zootella.base.state.TaskBody;
import org.zootella.base.state.Update;

public class IpTask extends OldClose {
	
	// Make

	public IpTask(Update up, Access device) {
		this.up = up; // We'll tell above when we're done
		this.device = device;
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	private final Update up;
	private final Access device;
	private final Task task;

	@Override public void close() {
		if (already()) return;
		close(task);
		up.send();
	}

	// Result
	
	public Result<Ip> result() { check(exception, ip); ip.check(); return ip; }
	private ProgramException exception;
	private Result<Ip> ip;
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Result<Ip> taskIp; // References thread() can safely set

		// A separate thread will call this method
		public void thread() {
			
			taskIp = Do.ip(device);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(ProgramException e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			ip = taskIp;
			close(IpTask.this); // We're done
		}
	}
}
