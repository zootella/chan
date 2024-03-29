package org.zootella.base.file;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.state.Close;
import org.zootella.base.state.Task;
import org.zootella.base.state.TaskBody;

public class OpenTask extends Close {
	
	// Make

	/** Open a file on the disk. */
	public OpenTask(Open open) {
		this.open = open;
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	public final Open open;
	private final Task task;

	@Override public void close() {
		if (already()) return;
		close(task);
	}

	// Result
	
	/** The File we opened, or throws the exception that made us give up. */
	public File result() { check(exception, file); return file; }
	private ProgramException exception;
	private File file;
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private File taskFile; // References thread() can safely set

		// A separate thread will call this method
		public void thread() {
				
			// Open the file
			taskFile = new File(open);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(ProgramException e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			file = taskFile;
			close(OpenTask.this); // We're done
		}
	}
}
