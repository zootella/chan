package org.zootella.base.pulse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pool {
	
	// Object
	
	/** Access the thread pool to submit a new task. */
	public synchronized ExecutorService get() {
		if (service == null)
			service = Executors.newCachedThreadPool(); // Good choice for a large number of quick asynchronous tasks
		return service;
	}
	private ExecutorService service;
	
	public synchronized void stop() {
		if (service != null) {
			service.shutdownNow(); // Stop the threads in the pool so the program can exit
			service = null;
		}
	}
}
