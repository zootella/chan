package org.zootella.main;

import javax.swing.SwingUtilities;

import org.zootella.base.process.Mistake;

public class Main {
	
	// Define
	
	/** true to build to release to users, false to include debugging information. */
	public static final boolean release = false;
	
	/** The name of this program. */
	public static final String name = "Chan";
	/** Text that describes the version of this program. */
	public static final String version = "2012 Jun 15";
	/** Text prefix for exchange codes. */
	public static final String flag = "CHAN";
	
	// Main

	// When the program runs, Java calls this main() method
	public static void main(String[] arguments) {
		SwingUtilities.invokeLater(new Runnable() { // Have the normal Swing thread call this run() method
			public void run() {
				try {

					// Make and start the program
					new Program();

				} catch (Throwable t) { Mistake.stop(t); } // Stop the program for an exception we didn't expect
			}
		});
	}
}
