package org.zootella.base.state;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.process.Mistake;
import org.zootella.base.time.Now;
import org.zootella.base.time.Speed;
import org.zootella.base.time.Time;
import org.zootella.base.user.Describe;

/** The static list and methods here pulse all the open objects in the program to move things forward. */
public class Pulse {

	// Start
	
	/** true when we've set Java to call run(), and it hasn't yet. */
	private static boolean start;
	/** true when an object has requested another pass up the pulse list. */
	private static boolean again;

	/**
	 * An object in the program has changed or finished.
	 * Pulse soon so the object that made it can notice and take the next step forward.
	 */
	public static void soon() {
		requireEventThread(); // Only the event thread can request a pulse soon

		// Start a pulse if one isn't already happening
		if (!start) { // No need to start a new pulse if we're doing one now already
			start = true;
			SwingUtilities.invokeLater(new MyRunnable()); // Have Java call run() below separately and soon
		}

		// Have the pulse loop up the list again
		again = true;
	}

	// Soon after soon() above calls SwingUtilities.invokeLater(), Java calls this run() method
	private static class MyRunnable implements Runnable {
		public void run() {
			try {
				pulse();
			} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
		}
	}

	// Pulse
	
	/** Pulse all the open objects in the program until none request another pulse soon. */
	private static void pulse() {
		
		long currentSpeed = speed.add(1, Time.second); // 1 event, get speed in events per second
		if (maximumSpeed < currentSpeed) maximumSpeed = currentSpeed; //TODO maybe skip middle if current speed is too high?
		countPulses++;
		countTimeOutside += now.age(); // Measure how long we were outside
		now = new Now();
		
		// Pulse up the list in many passes until no object requests another pulse soon
		while (again) {
			again = false; // Don't loop again unless an object we pulse below calls soon() above
			
			if (now.expired(Time.delay / 2)) { countHitLimit++; break; } // Quit early if we're over the time limit
			
			// Pulse up the list in a single pass
			for (int i = list.size() - 1; i >= 0; i--) { // Loop backwards to pulse contained objects before the older objects that made them
				countLoops++;
				Close c = list.get(i);
				if (!c.closed()) { // Skip closed objects
					countObjects++;
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
		start = false; // Allow the next call to soon to start a new pulse
		
		countTimeInside += now.age(); // Measure how long we were inside
		now = new Now();
	}
	
	// List

	/** Add a new object that extends Close to the program's list of open objects. */
	public static void add(Close c) { // The Close constructor already checked that only the event thread can do this
		list.add(c); // It's safe to add to the end even during a pulse because we loop by index number
	}

	/** Remove objects that got closed from our list. */
	private static void clear() {
		for (int i = list.size() - 1; i >= 0; i--) { // Loop backwards so we can remove things along the way
			if (list.get(i).closed()) list.remove(i);
		}
	}

	/** The program's list of all objects that extend Close, and aren't closed yet. */
	private static final List<Close> list = new ArrayList<Close>();
	
	// Ding

	/** true when we are set to pulse again soon. */
	public static boolean isSetToPulseSoon() { return start; }
	/** The time when we most recently started or finished a pulse. */
	public static Now timeStartedOrFinished() { return now; }
	
	// Monitor

	/** The time when we last entered or left the pulse function. */
	private static Now now = new Now();
	
	/** The speed at which pulses are happening right now. */
	private static final Speed speed = new Speed(Time.second); // Keep the most recent 1 second of data
	/** The maximum speed we recorded as the program ran. */
	private static long maximumSpeed;
	
	/** How many pulses have happened. */
	private static long countPulses;
	/** How many loops have happened within all the pulses. */
	private static long countLoops;
	/** How many objects we've pulsed within all the loops and pulses. */
	private static long countObjects;
	/** How many pulses have gone over the time limit and quit early. */
	private static long countHitLimit;
	/** How long the program has spent inside the pulse function, in milliseconds. */
	private static long countTimeInside;
	/** How long the program has spent outside the pulse function, in milliseconds. */
	private static long countTimeOutside;

	/** Compose text about how efficiently the program has been running. */
	public static String composeEfficiency() {
		
		StringBuffer s = new StringBuffer();
		s.append("The average pulse looped up a list of [" + Describe.average(countLoops, countObjects) + "] objects [" + Describe.average(countPulses, countLoops) + "] times.");
		s.append("The average pulse took [" + Describe.average(countPulses, countTimeInside) + "] milliseconds, and [" + Describe.percent(countHitLimit, countPulses) + "] hit the time limit.");
		s.append("The program spent [" + Describe.percent(countTimeInside, countTimeInside + countTimeOutside) + "] of its time pulsing.");
		s.append("The fastest the program pulsed was [" + Describe.commas(maximumSpeed) + "] pulses per second");
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
	public static void requireEventThread() {
		if (!SwingUtilities.isEventDispatchThread()) Mistake.stop(new ProgramException("method limited to event thread only"));
	}
}
