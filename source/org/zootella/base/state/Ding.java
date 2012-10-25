package org.zootella.base.state;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Timer;

import org.zootella.base.process.Mistake;
import org.zootella.base.time.Time;

/** The program's single Ding object requests a pulse every 200 milliseconds just in case nothing is happening. */
public class Ding extends Close {
	
	/** Make a Ding that will pulse the program so timeouts get noticed. */
	public Ding() {
		timer = new Timer((int)Time.delay / 2, new MyActionListener()); // Check every half delay to catch nothing happening sooner
		timer.setRepeats(true);
		timer.start();
	}
	
	/** Our Timer set to repeat. */
	private Timer timer;

	/** Close our Ding so it never pulses the program again. */
	public void close() {
		if (already()) return;
		timer.stop(); // Stop and discard timer, keeping it might prevent the program from closing
		timer = null;
	}

	// When the timer goes off, Java calls this method
	private class MyActionListener extends AbstractAction {
		public void actionPerformed(ActionEvent a) {
			try {
				if (closed()) return; // Don't do anything if we're closed
				
				if (!Pulse.isSetToPulseSoon() &&                       // If the program isn't already pulsing or set to start, and
					Pulse.timeStartedOrFinished().expired(Time.delay)) // It's been longer than the delay since the last pulse finished
					Pulse.soon();                                      // Have the program pulse soon to notice things that have timed out

			} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
		}
	}
}
