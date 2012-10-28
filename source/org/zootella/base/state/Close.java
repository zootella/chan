package org.zootella.base.state;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.process.Log;
import org.zootella.base.process.Mistake;

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
	 * Notice things inside your object that have changed or finished, and do the next step to move forward.
	 */
	public void pulse() {}
	
	/**
	 * Override this method, and the program will call it periodically.
	 * Compose text and information for the user based on the new current state of things.
	 */
	public void pulseUser() {}

	// Object

	/**
	 * Keep track of a new object that needs to be closed.
	 * This automatically runs before execution enters the constructor of an object that extends Close.
	 */
	public Close() {
		Pulse.requireEventThread(); // Only the event thread can make a new Close object
		Pulse.add(this); // Add this new object that extends Close to the program's list of open objects
		Pulse.soon(); // Have the program pulse this new object soon
	}
	
	/** true once this object that extends Close has been closed, and promises to not change again. */
	public boolean closed() { return objectClosed; }
	private boolean objectClosed; // Private so objects that extend Close can't get to this

	/**
	 * Mark this object that extends Close as closed, and only do this once.
	 * Start your close() method with the code "if (already()) return;".
	 * The first time already() runs, it marks this object as closed and returns false.
	 * Try calling it again, and it will just return true.
	 */
	public boolean already() {
		if (objectClosed) return true; // We're already closed, return true to return from the close() method
		objectClosed = true;           // Mark this object that extends Close as now permanently closed
		Pulse.soon();                  // Have the program pulse soon so the object that made this one can notice it finished
		return false;                  // Return false to run the contents of the close() method this first and only time
	}
	
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
		Pulse.requireEventThread(); // Only the event thread can close an object
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

	/** Write out diagnostic text for the programmer. */
	public static void log(String s) { Log.log(s); }
}
