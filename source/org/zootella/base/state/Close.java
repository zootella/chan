package org.zootella.base.state;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.process.Mistake;
import org.zootella.base.time.Now;
import org.zootella.base.user.Describe;

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

	// Object

	/**
	 * Keep track of a new object that needs to be closed.
	 * This automatically runs before execution enters the constructor of an object that extends Close.
	 */
	public Close() {
		requireEventThread(); // Only the event thread can make a new object
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
		requireEventThread(); // Only the event thread can close an object
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
	
	// Pulse
	
	/** true when we've set Java to call run(), and it hasn't yet. */
	private static boolean start;
	/** true when an object has requested another pass up the pulse list. */
	private static boolean again;

	/**
	 * An object in the program has changed or finished.
	 * Pulse up the list soon so the object that made it can notice and take the next step forward.
	 */
	public static void pulseSoon() {
		requireEventThread(); // Only the event thread can request a pulse soon

		// Start a pulse if one isn't already happening
		if (!start) { // No need to start a new pulse if we're doing one now already
			start = true;

			SwingUtilities.invokeLater(new Runnable() { // This code calls invokeLater() and returns immediately
				public void run() { // Soon after, the same thread but a separate event calls this run() method
					try {
						pulseAll();
					} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
				}
			});
		}

		// Have the pulse loop up the list again
		again = true;
	}

	/** Pulse all the open objects in the program until none request another pulse soon. */
	private static void pulseAll() {
		
		countPulses++;
		countTimeOutsidePulse += now.age();
		now = new Now();
		
		// Pulse up the list in many passes until no object requests another pulse soon
		while (again) {
			again = false;
			
			if (now.expired(pulseLimit)) { countLimits++; break; }
			
			// Pulse up the list in a single pass
			for (int i = list.size() - 1; i >= 0; i--) { // Loop backwards to pulse contained objects before the older objects that made them
				countLoops++;
				Close c = list.get(i);
				if (!c.closed()) { // Skip closed objects
					try {
						countObjects++;
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
		start = false; // Allow the next call to pulse soon to start a new pulse
		
		countTimeInsidePulse += now.age();
		now = new Now();
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

	/** The program's list of all objects that extend Close, and aren't closed yet. */
	private static final List<Close> list = new ArrayList<Close>();

	// Monitor

	/** The program pulses 5 times a second. */
	public static final int pulseInterval = 200;
	/** Don't let a single pulse last more than 1/10 second. */
	public static final int pulseLimit = 100;

	/** The time when we last entered or left the pulse function. */
	private static Now now = new Now();
	
	/** How many pulses have happened. */
	private static long countPulses;
	/** How many loops have happened within all the pulses. */
	private static long countLoops;
	/** How many objects we've pulsed within all the loops and pulses. */
	private static long countObjects;
	/** How many pulses have gone over the time limit and quit early. */
	private static long countLimits;
	/** How long the program has spent inside the pulse function, in milliseconds. */
	private static long countTimeInsidePulse;
	/** How long the program has spent outside the pulse function, in milliseconds. */
	private static long countTimeOutsidePulse;

	/** Compose text about how efficiently the program has been running. */
	public static String composeEfficiency() {
		
		StringBuffer s = new StringBuffer();
		s.append("The average pulse looped up a list of [" + Describe.average(countLoops, countObjects) + "] objects [" + Describe.average(countPulses, countLoops) + "] times.");
		s.append("The average pulse took [" + Describe.average(countPulses, countTimeInsidePulse) + "] milliseconds, and [" + Describe.percent(countLimits, countPulses) + "] hit the time limit.");
		s.append("The program spent [" + Describe.percent(countTimeInsidePulse, countTimeInsidePulse + countTimeOutsidePulse) + "] of its time pulsing.");
		return s.toString();
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
	
	// Require

	/** Make sure this is the event thread, or exit the program without returning. */
	private static void requireEventThread() {
		if (!SwingUtilities.isEventDispatchThread()) Mistake.stop(new ProgramException("method limited to event thread only"));
	}

	// Log

	/** Write out diagnostic text for the programmer. */
	public static void log(String s) { System.out.println((new Now()).toString() + " " + s); }
}
