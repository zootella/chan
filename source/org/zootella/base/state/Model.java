package org.zootella.base.state;

import java.util.HashSet;
import java.util.Set;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.time.Delay;
import org.zootella.base.time.OldPulse;

/** An object has a Model that extends this class to keep View objects above up to date. */
public abstract class Model extends Close {

	// Core

	/** Setup the core of this new object that extends Model. */
	public Model() {
		delay = new Delay();
		update = new Update();
		views = new HashSet<View>();
	}
	
	/** Our Delay that keeps us from updating the screen so frequently the whole program would slow down. */
	private final Delay delay;
	/** Our Update acts right away. */
	private final Update update;
	/** Our list of View objects above viewing us. */
	private final Set<View> views;
	
	/** The object this Model is a part of is closed, have Model tell all the views above to close. */
	public void close() {
		if (already()) return;
		close(delay);
		close(oldPulse);
		for (View view : new HashSet<View>(views)) // Copy the list so we can change the original
			view.vanish();                         // This removes the view from views
		views.clear();                             // It should be empty now, but clear it just to be sure
	}

	// Add and remove

	/** View tells Model to connect the two, afterwards Model will tell View when things change. */
	public void add(View view) { views.add(view); } // Add the View to our list
	/** View tells Model to disconnect the two, Model won't notify View anymore. */
	public void remove(View view) { views.remove(view); } // Remove the View from our list
	
	// Send and receive

	//TODO added update to split into changed() and progress()
	/** The object this Model is a part of has changed, have Model tell all the views above to update right away. */
	public void changed() { update.send(); } // Right away
	/** The object this Model is a part of has changed, have Model tell all the views above to update soon. */
	public void progress() { delay.send(); } // After the delay
	@Override public void pulse() {
		for (View view : views)
			view.refresh(); // This Model has changed, tell all our views above
	}

	// Pulse

	/** If this Model has something guaranteed to change in time, like an age, have it pulse views above. */
	public void modelPulse() {
		if (oldPulse == null)
			oldPulse = new OldPulse();
	}
	private OldPulse oldPulse;//TODO confirm nobody's using this and get rid of it
	
	
	
	
	
	
	
	
	public static String describe(Result<?> result) {
		if (result == null) return "";
		try {
			return result.result().toString();
		} catch (ProgramException e) {
			return e.toString();
		}
	}
	
	public static String describeTime(Result<?> result) {
		if (result == null) return "";
		return result.duration.toString();
	}
	
	public static String describeError(Result<?> result) {
		if (result == null) return "";
		if (result.exception == null) return "";
		return result.exception.toString();
	}
	
	
	
}
