package org.zootella.base.state;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.SwingUtilities;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.process.Mistake;

/** Make a Task to run some code in a separate thread. */
public class Task extends Close {
	
	// Pool
	
	/** The program's thread pool. */
	private static ExecutorService pool;
	
	// Make

	/** Make a Task to have a separate thread run the code in body now. */
	public Task(TaskBody body) {
		if (pool == null) // Create the thread pool the first time this runs
			pool = Executors.newCachedThreadPool(); // Good choice for many quick tasks

		this.body = body;
		future = pool.submit(new ThreadRun()); // Have a thread from the pool call run() below now
	}

	private final TaskBody body;
	private final Future<?> future;
	
	/** Interrupt our Task thread. */
	@Override public void close() {
		if (already()) return;
		future.cancel(true); // true to interrupt if running
	}
	
	// Run
	
	// When the constructor makes thread above, thread calls the run() method here
	private class ThreadRun implements Runnable {
		public void run() {
			try { body.thread(); } // Call the code we were given
			catch (ProgramException e) { programException = e; } // A ProgramException we expect and save
			catch (Throwable t) { throwable = t; } // An exception isn't expected, and stops the program
			SwingUtilities.invokeLater(new EventRun()); // We're done, send an event
		} // When thread exits run(), it closes
	}	
	
	private ProgramException programException;
	private Throwable throwable;

	// Soon after thread calls invokeLater() above, the normal event thread calls run() here
	private class EventRun implements Runnable {
		public void run() {
			if (closed()) return;                           // Do nothing once closed
			if (throwable != null) Mistake.stop(throwable); // An exception isn't expected, stop the program
			try {
				close(Task.this);                           // Mark this Task closed
				body.done(programException);                // Call the given done() method giving it the ProgramException we got
			} catch (Throwable t) { Mistake.stop(t); }      // Stop the program for an exception we didn't expect
		}
	}
}
