package org.zootella.base.pulse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.zootella.base.state.Close;

public class Pool extends Close {
	
	// Instance
	
	/** The program's single Pool object, which must be closed. */
	public static final Pool pool = new Pool();
	
	// Object
	
	/** Make a pool of threads that will run tasks. */
	public Pool() {
		service = Executors.newCachedThreadPool(); // Good choice for a large number of quick asynchronous tasks
	}

	/** Access the thread pool to submit a new task. */
	public ExecutorService service() {
		confirmOpen(); // Make sure we haven't been closed
		return service;
	}
	private final ExecutorService service;
	
	@Override public void close() {
		if (already()) return;
		service.shutdownNow(); // Stop the threads in the pool so the program can exit
	}
}
