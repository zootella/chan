package org.zootella.base.pulse;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Timer;

import org.zootella.base.process.Mistake;
import org.zootella.base.time.Time;

/** The program's single Ding object requests a pulse every 200 milliseconds just in case nothing is happening. */
public class Ding {
	
	/** Start our Ding that will pulse the program so timeouts get noticed. */
	public synchronized void start() {
		if (timer == null) {
			timer = new Timer((int)Time.delay / 2, new MyActionListener()); // Check every half delay to catch nothing happening sooner
			timer.setRepeats(true);
			timer.start();
		}
	}
	
	/** Stop our Ding so it won't pulse the program again. */
	public synchronized void stop() {
		if (timer != null) {
			timer.stop(); // Stop and discard timer, keeping it might prevent the program from closing
			timer = null; // Discard the timer object so a future call to start() can start things again
		}
	}
	
	/** Our Timer set to repeat. */
	private volatile Timer timer;

	// When the timer goes off, Java calls this method
	private class MyActionListener extends AbstractAction {
		public void actionPerformed(ActionEvent a) {
			try {
				if (timer == null) return; // Don't do anything if we're stopped
				
				Pulse.pulse.ding(); // Pulse soon if we haven't pulsed in a while

			} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
		}
	}
}
