package org.zootella.base.pulse;

import javax.swing.SwingUtilities;

import org.zootella.base.process.Log;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.base.time.Ago;
import org.zootella.base.time.Time;

/** The program's single pulse object pulses all the open objects in the program to move things forward. */
public class Pulse {
	
	// Instance
	
	/** The program's single Pulse object. */
	public static final Pulse pulse = new Pulse();
	
	// Contents

	/** A thread pool to run tasks. */
	public final Pool pool = new Pool();
	
	/** A timer that will cause a pulse to happen even if nothing else does. */
	private final Ding ding = new Ding();

	/** Record efficiency and performance statistics. */
	public final Monitor monitor = new Monitor();
	
	// Stop
	
	/** When the program or test is done, make sure you closed every object, and stop things so the process can exit. */
	public void stop() {
		
		// Stop things so the process will be able to exit
		pool.stop(); // Shut down the thread pool
		ding.stop(); // Stop and discard the timer

		// Make sure the program closed every object before trying to exit
		clear();                // Remove closed objects from the list
		int size = list.size(); // See how many are left
		if (size != 0) {        // We should have closed them all, but didn't
		
			StringBuffer s = new StringBuffer(); // Compose and return text about the objects still open by mistake
			s.append(size + " objects open:\n");
			for (int i = 0; i < size; i++) {
				Close c = list.get(i);
				if (Close.open(c)) { // Skip closed objects
					s.append(c.toString() + "\n");
				}
			}
			
			Mistake.close(s.toString()); // Log the list of objects and exit the process
		}
		
		// Log performance and efficiency statistics
		Log.log(monitor.describeEfficiency());
	}

	// Start
	
	/** true when we've set Java to call run(), and it hasn't yet. */
	private boolean start;
	/** true when an object has requested another pass up the pulse list. */
	private boolean again;
	
	/** Pulse soon if we haven't pulsed in a while. */
	public void ding() {
		if (!start &&       // If the program isn't already pulsing or set to start, and
			monitor.ding()) // It's been longer than the delay since the last pulse finished
			soon();         // Have the program pulse soon to notice things that have timed out
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
				pulseAll();
			} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
		}
	}

	// Pulse
	
	/** Pulse all the open objects in the program until none request another pulse soon. */
	private void pulseAll() {
		monitor.start();
		
		// Pulse up the list in many passes until no object requests another pulse soon
		while (again) {
			again = false; // Don't loop again unless an object we pulse below calls soon() above
			if (monitor.loop()) break; // Quit early this pulse goes over the time limit
			
			// Pulse up the list in a single pass
			for (int i = list.size() - 1; i >= 0; i--) { // Loop backwards to pulse contained objects before the older objects that made them
				Close c = list.get(i);
				if (Close.open(c)) { // Skip closed objects
					try {
						c.pulse(); // Pulse the object so it notices things that have finished and moves to the next step
					} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
				}
			}
		}
		
		// In a single pass after that, pulse up the list to have objects compose information for the user
		if (screen.enough()) { // Only update the screen 5 times a second
			for (int i = list.size() - 1; i >= 0; i--) {
				Close c = list.get(i);
				if (Close.open(c)) { // Skip closed objects
					try {
						c.pulseScreen(); // Pulse the object to have it compose text for the user to show current information
					} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
				}
			}
		}
		
		clear(); // Remove closed objects from the list all at once at the end
		monitor.end(list.size());
		start = false; // Allow the next call to soon to start a new pulse
	}
	
	/** Make sure we don't update the screen too frequently. */
	private Ago screen = new Ago(Time.delay);
	
	// List

	/** Add a new object that extends Close to the program's list of open objects. */
	public void add(Close c) {
		list.add(c); // It's safe to add to the end even during a pulse because we loop by index number
		ding.start(); // Start the ding if it's not started already
	}

	/** Remove objects that got closed from our list. */
	private void clear() {
		for (int i = list.size() - 1; i >= 0; i--) { // Loop backwards so we can remove things along the way
			Close c = list.get(i);
			if (Close.done(c)) // Only remove closed objects
				list.remove(i);
		}
	}
	
	/** A list of all the objects in the program right now that extend Close, and aren't closed yet. */
	private final CloseList list = new CloseList();
}
