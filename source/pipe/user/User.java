package pipe.user;

import pipe.main.Program;
import base.state.Close;

public class User extends Close {

	// Object
	
	public User(Program program) {

		main = new MainFrame(program);
		info = new InfoFrame(program);
		icon = new MainIcon(program);

		show(true);
	}
	
	public final MainFrame main;
	public final InfoFrame info;
	public final MainIcon icon;

	private boolean show;
	public void show(boolean b) {
		if (show == b) return;
		show = b;

		main.frame.setVisible(show);
		icon.show(!show);
	}

	@Override public void close() {
		if (already()) return;
		
		close(main);
		close(info);
		close(icon);
	}
}
