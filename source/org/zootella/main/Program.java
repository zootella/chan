package org.zootella.main;

import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.base.state.Ding;
import org.zootella.base.state.Pulse;
import org.zootella.base.store.Store;
import org.zootella.base.user.Face;

public class Program extends Close {
	
	public Program() {

		Face.blend(); // Tell Java how to show the program's user interface

		store = new Store(); // Load data and preferences from last time the program ran
		core = new Core(this); // Make the core that does everything
		user = new User(this); // Put the window on the screen to let the user interact with it
		
		ding = new Ding();
		
		unit1 = new NullUnit();
		unit2 = new NullUnit();
		unit3 = new NullUnit();
		unit4 = new NullUnit();
		unit5 = new NullUnit();
	}
	
	public final Store store;
	public final Core core;
	public final User user;
	
	private final Ding ding;
	
	private NullUnit unit1, unit2, unit3, unit4, unit5;

	@Override public void close() {
		if (already()) return;
		
		close(ding);
		close(user);
		close(core);
		
		close(unit1);
		close(unit2);
		close(unit3);
		close(unit4);
		close(unit5);
		
		store.save();
		
		Mistake.closeCheck();
		log(Pulse.pulse.composeEfficiency());
	}
}
