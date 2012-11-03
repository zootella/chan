package org.zootella.base.state;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.zootella.base.process.Mistake;
import org.zootella.base.time.Now;
import org.zootella.base.time.Speed;
import org.zootella.base.time.Time;
import org.zootella.base.user.Describe;

/** The program's single pulse object lists and pulses all the open objects in the program to move things forward. */
public class Pulse {
	
	// Instance
	
	/** The program's single Pulse object. */
	public static final Pulse pulse = new Pulse();

	// Start
	
	/** true when we've set Java to call run(), and it hasn't yet. */
	private boolean start;
	/** true when an object has requested another pass up the pulse list. */
	private boolean again;
	
	/** Pulse soon if we haven't pulsed in a while. */
	public void ding() {
		if (!start &&                // If the program isn't already pulsing or set to start, and
			now.expired(Time.delay)) // It's been longer than the delay since the last pulse finished
			soon();                  // Have the program pulse soon to notice things that have timed out
	}

	/**
	 * An object in the program has changed or finished.
	 * Pulse soon so the object that made it can notice and take the next step forward.
	 * It's safe to call this from the event thread or a Task thread, and it will return quickly.
	 */
	public void soon() {
		if (SwingUtilities.isEventDispatchThread()) {
			soonDo();
		} else {
			SwingUtilities.invokeLater(new Runnable() { // Have the normal Swing thread call this run() method
				public void run() {
					try {
						soonDo();
					} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
				}
			});
		}
	}
	private void soonDo() {

		// Start a pulse if one isn't already happening
		if (!start) { // No need to start a new pulse if we're doing one now already
			start = true;
			SwingUtilities.invokeLater(new MyRunnable()); // Have Java call run() below separately and soon
		}

		// Have the pulse loop up the list again
		again = true;
	}

	// Soon after soon() above calls SwingUtilities.invokeLater(), Java calls this run() method
	private class MyRunnable implements Runnable {
		public void run() {
			try {
				pulse();
			} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
		}
	}

	// Pulse
	
	/** Pulse all the open objects in the program until none request another pulse soon. */
	private void pulse() {
		
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
				if (Close.open(c)) { // Skip closed objects
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
			if (Close.open(c)) { // Skip closed objects
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
	public void add(Close c) {
		list.add(c); // It's safe to add to the end even during a pulse because we loop by index number
	}

	/** Remove objects that got closed from our list. */
	private void clear() {
		for (int i = list.size() - 1; i >= 0; i--) { // Loop backwards so we can remove things along the way
			if (list.get(i).closed()) list.remove(i);
		}
	}

	/** The program's list of all objects that extend Close, and aren't closed yet. */
	private final CloseList list = new CloseList();
	
	// Monitor

	/** The time when we last entered or left the pulse function. */
	private Now now = new Now();
	
	/** The speed at which pulses are happening right now. */
	private final Speed speed = new Speed(Time.second); // Keep the most recent 1 second of data
	/** The maximum speed we recorded as the program ran. */
	private long maximumSpeed;
	
	/** How many pulses have happened. */
	private long countPulses;
	/** How many loops have happened within all the pulses. */
	private long countLoops;
	/** How many objects we've pulsed within all the loops and pulses. */
	private long countObjects;
	/** How many pulses have gone over the time limit and quit early. */
	private long countHitLimit;
	/** How long the program has spent inside the pulse function, in milliseconds. */
	private long countTimeInside;
	/** How long the program has spent outside the pulse function, in milliseconds. */
	private long countTimeOutside;

	/** Compose text about how efficiently the program has been running. */
	public String composeEfficiency() {
		
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
	public String confirmAllClosed() {
		
		clear(); // Remove closed objects from the list
		
		int size = list.size();
		if (size == 0) return ""; // Good, we had closed them all already
		
		StringBuffer s = new StringBuffer(); // Compose and return text about the objects still open by mistake
		s.append(size + " objects open:\n");
		for (int i = 0; i < size; i++) {
			Close c = list.get(i);
			if (Close.open(c)) { // Skip closed objects
				s.append(c.toString() + "\n");
			}
		}
		return s.toString();
	}
}
