package org.zootella.base.encrypt.secret;

import javax.crypto.Cipher;

import org.zootella.base.data.Bin;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.size.move.Move;
import org.zootella.base.state.Close;
import org.zootella.base.state.Task;
import org.zootella.base.state.TaskBody;
import org.zootella.base.state.Update;

public class SecretTask extends Close {

	public SecretTask(Update up, Cipher cipher, int mode, Bin source, Bin destination) {
		this.up = up; // We'll tell update when we're done
		this.cipher = cipher;
		this.mode = mode;
		this.source = source;
		this.destination = destination;
		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}
	
	private final Update up;
	private final Cipher cipher;
	private final int mode;
	private final Bin source;
	private final Bin destination;
	private final Task task;

	@Override public void close() {
		if (already()) return;
		close(task);
		up.send();
	}

	public Move result() { check(exception, data); return data; }
	private ProgramException exception;
	private Move data;
	
	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Move taskMove; // References thread() can safely set

		// A separate thread will call this method
		public void thread() {
			
			taskMove = Secret.process(cipher, mode, source, destination);
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(ProgramException e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			data = taskMove;
			close(SecretTask.this); // We're done
		}
	}
}
