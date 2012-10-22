package org.zootella.base.file;

import java.util.List;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.state.OldClose;
import org.zootella.base.state.Task;
import org.zootella.base.state.TaskBody;
import org.zootella.base.state.Update;

public class ListTask extends OldClose {

	public ListTask(Update up, Path folder) {
		this.up = up; // We'll tell update when we're done
		this.folder = folder;
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	private final Update up;
	private final Path folder;
	private final Task task;

	@Override public void close() {
		if (already()) return;
		close(task);
		up.send();
	}

	public List<Name> result() { check(exception, list); return list; }
	private ProgramException exception;
	private List<Name> list;
	
	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private List<Name> taskList; // References thread() can safely set

		// A separate thread will call this method
		public void thread() {
			
			taskList = folder.list();
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(ProgramException e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			list = taskList;
			close(ListTask.this); // We're done
		}
	}
}
