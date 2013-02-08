package org.zootella.base.pulse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** A thread pool to run tasks. */
public class Pool {
	
	/** Access the thread pool to submit a new task. */
	public ExecutorService get() {
		if (service == null)
			service = Executors.newCachedThreadPool(); // Good choice for a large number of quick asynchronous tasks
		return service;
	}
	private ExecutorService service;
	
	/** When the program or test is done, stop the thread pool so the process can exit. */
	public void stop() {
		if (service != null) {
			service.shutdownNow(); // Stop the threads in the pool so the program can exit
			service = null;        // Discard the service so a future call to get() can start things again
		}
	}
}
