package org.zootella.base.state;

import java.util.ArrayList;
import java.util.List;

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
		add(this); // Add this new object that extends Close to the program's list of open objects
	}
	
	/** true once this object that extends Close has been closed, and promises to not change again. */
	public boolean closed() { return objectClosed; }
	private boolean objectClosed; // Private so objects that extend Close can't get to this

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
	public static int checkAll() {
		check();
		return listSize();
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

	/** The program's list of objects that extend Close, and aren't closed yet. */
	private static final List<Close> list = new ArrayList<Close>();
	/** Add a new object to the end of the program's list. */
	private static synchronized void add(Close c) { list.add(c); }
	/** Remove the object at index i from the program's list. */
	private static synchronized void remove(int i) { list.remove(i); }
	/** Print information about the objects still in the list, and return how many there are. */
	private static synchronized int check() {
		int size = list.size();
		if (size != 0) {
			System.out.print(size + " objects open:\n");
			for (Close c : list)
				System.out.print(c.toString() + "\n");
		}
		return size;
	}
	
	// Pulse

	/** How many objects that extend Close are in the program's list. */
	private static synchronized int listSize() {
		return list.size();
	}

	/** Remove closed objects from the program's list so it only contains objects that need to be closed. */
	private static synchronized void listClear() {
		for (int i = list.size() - 1; i >= 0; i--) {
			Close c = list.get(i);
			
			if (c.closed()) list.remove(i);
		}
	}

	/** Make a single pass from the end of the list to the start, calling pulse() on each object. */
	private static synchronized void listPulse() {
		for (int i = list.size() - 1; i >= 0; i--) {
			Close c = list.get(i);
			
			if (!c.closed()) c.pulse();
		}
	}

	/** Make a single pass from the end of the list to the start, calling pulseUser() on each object. */
	private static synchronized void listPulseUser() {
		for (int i = list.size() - 1; i >= 0; i--) {
			Close c = list.get(i);
			
			if (!c.closed()) c.pulseUser();
		}
	}

	// Log

	/** Write out diagnostic text for the programmer. */
	public static void log(String s) { System.out.println((new Now()).toString() + " " + s); }
}
