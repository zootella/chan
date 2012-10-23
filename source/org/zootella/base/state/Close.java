package org.zootella.base.state;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.process.Mistake;
import org.zootella.base.time.Now;

/** Have your object extend Close so the program will pulse it, and notice if you forget to later call its close() method. */
public abstract class Close {
	
	// Override

	/**
	 * Your object that extends Close must have this method.
	 * Close your objects inside, put away resources, and never change again.
	 */
	public abstract void close();

	/**
	 * Override this method, and the program will call it periodically.
	 * Notice things inside your object that have changed or finished, and do the next steps to move forward.
	 */
	public void pulse() {}
	
	/**
	 * Override this method, and the program will call it periodically.
	 * Compose text and information for the user based on the new current state of things.
	 */
	public void pulseUser() {}

	// Core

	/**
	 * Keep track of a new object that needs to be closed.
	 * This automatically runs before execution enters the constructor of an object that extends Close.
	 */
	public Close() {
		requireEventThread();
		add(this); // Add this new object that extends Close to the program's list of open objects
	}
	
	/** true once this object that extends Close has been closed, and promises to not change again. */
	public boolean closed() { return objectClosed; }
	private volatile boolean objectClosed;
	// Private so objects that extend Close can't get to this
	// A Task thread should never check if an object is closed, volatile in case one does 

	/**
	 * Mark this object that extends Close as closed, and only do this once.
	 * Start your close() method with the code if (already()) return;.
	 * The first time already() runs, it marks this object as closed and returns false.
	 * Try calling it again, and it will just return true.
	 */
	public boolean already() {
		if (objectClosed) return true; // We're already closed, return true to return from the close() method
		objectClosed = true;           // Mark this object that extends Close as now permanently closed
		return false;                  // Return false to run the contents of the close() method this first and only time
	}

	// Program

	/** Before the program closes, make sure every object with a close() method had it run. */
	/*
	public static int checkAll() {
		check();
		return listSize();
	}
	*/
	
	// Check

	/** Make sure this object isn't closed before doing something that would change it. */
	public void confirmOpen() { if (objectClosed) throw new IllegalStateException(); }

	/** Make sure this object is closed, throw e if given, and make sure o exists. */
	public void check(ProgramException e, Object o) {
		if (!objectClosed) throw new IllegalStateException();
		if (e != null) throw e;
		if (o == null) throw new NullPointerException();
	}

	// Help

	/** Close c ignoring null and exceptions. */
	public static void close(Close c) {
		requireEventThread();
		if (c == null) return;
		try { c.close(); } catch (Throwable t) { Mistake.log(t); } // Keep going to close the next object
	}

	/** true if c is null. */
	public static boolean no(Close c) { return c == null; }
	/** true if c exists. */
	public static boolean is(Close c) { return c != null; }
	/** true if c exists and is not yet closed. */
	public static boolean open(Close c) { return c != null && !c.closed(); }
	/** true if c exists and is closed. */
	public static boolean done(Close c) { return c != null && c.closed(); }

	// List

	/** The program's list of all objects that extend Close, and aren't closed yet. */
	private static final List<Close> list = new ArrayList<Close>();
	/** Add a new object to the end of the program's list. */
//	private static synchronized void add(Close c) { list.add(c); }
	/** Remove the object at index i from the program's list. */
//	private static synchronized void remove(int i) { list.remove(i); }
	
	// Soon
	
	/** true when we've set Java to call run(), and it hasn't yet. */
	private static boolean set;
	private static boolean again;
	
	/**
	 * Have this Update call the receive() method you gave it in a separate event.
	 * Call send() several times in a row, and receive() will only happen once.
	 * It's safe to call this from whatever thread you want.
	 */
	
	public static void pulseSoon() {
		requireEventThread();
		
		if (!set) {
			set = true;
			SwingUtilities.invokeLater(new MyRunnable()); // Have Java call run() below separately and soon
		}
		
		again = true;
	}

	// Soon after send() above calls SwingUtilities.invokeLater(), Java calls this run() method
	private static class MyRunnable implements Runnable {
		public void run() {
			try {
				pulseAll();                            // Call our given receive() method
			} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
		}
	}	
	
	// Pulse

	
	
	
	
	
	
	//do it all here
	//synchronized, so a task thread that's created a new close object will wait to get into add
	//but, realize that run will call into add
	
	// Don't run for more than 200ms
	// Don't loop more times than the list was originally long
	// Keep track of the size of the list
	// Keep track of how many times we loop in a run
	// Keep track of how long runs take
	//keep track of how long the program spends running versus how long it spends not running, have both time counts
	
	//send update message to run immediately
	//setup timer to pulse every 200ms in addition to running immediately
	
	//catch exceptions in the same way that update does now, that all moves in here too
	
	//maybe also runningNow, so if you set soon

	private static void pulseAll() {
		
		
		
		// Pulse up the list in many passes until no object requests another pulse soon
		while (again) {
			again = false;
			
			// Pulse up the list in a single pass
			for (int i = list.size() - 1; i >= 0; i--) { // Loop backwards to pulse contained objects before the older objects that made them
				Close c = list.get(i);
				if (!c.closed()) { // Skip closed objects
					try {
						c.pulse(); // Pulse the object so it notices things that have finished and moves to the next step
					} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
				}
			}
		}

		// In a single pass after that, pulse up the list to have objects compose information for the user
		for (int i = list.size() - 1; i >= 0; i--) {
			Close c = list.get(i);
			if (!c.closed()) { // Skip closed objects
				try {
					c.pulseUser(); // Pulse the object to have it compose text for the user to show current information
				} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
			}
		}
		
		

		
		clear(); // Remove closed objects from the list all at once at the end
		
		set = false;
	}

	/** Add a new object that extends Close to the program's list of open objects. */
	private static void add(Close c) {
		list.add(c); // Adding on the end is OK because we loop by index number
	}

	/** Remove objects that got closed from our list. */
	private static void clear() {
		for (int i = list.size() - 1; i >= 0; i--) { // Loop backwards so we can remove things along the way
			if (list.get(i).closed()) list.remove(i);
		}
	}
	
	
	/**
	 * Call before the program exits to make sure we've closed every object.
	 * @return Text about objects still open by mistake, or blank if there's no problem
	 */
	public static String confirmAllClosed() {
		
		clear(); // Remove closed objects from the list
		
		int size = list.size();
		if (size == 0) return ""; // Good, we had closed them all already
		
		StringBuffer s = new StringBuffer(); // Compose and return text about the objects still open by mistake
		s.append(size + " objects open:\n");
		for (Close c : list)
			s.append(c.toString() + "\n");
		return s.toString();
	}
	

	// Log

	/** Write out diagnostic text for the programmer. */
	public static void log(String s) { System.out.println((new Now()).toString() + " " + s); }
	
	
	
	
	//more down here to move elsewhere

	/** Make sure this is the event thread, or exit the program without returning. */
	private static void requireEventThread() {
		if (!SwingUtilities.isEventDispatchThread()) Mistake.stop(new ProgramException("method limited to event thread only"));
	}


	
	
}
