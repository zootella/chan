package pipe.user;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import pipe.main.Program;
import base.process.Mistake;
import base.state.Close;
import base.state.View;
import base.user.Refresh;
import base.user.Screen;
import base.user.panel.Cell;
import base.user.panel.Panel;
import base.user.widget.Button;
import base.user.widget.Label;
import base.user.widget.SelectTextArea;

/** The Info window that shows advanced statistics and diagnostic information. */
public class InfoFrame extends Close {

	public InfoFrame(Program program) {
		this.program = program;
		
		refreshAction = new RefreshAction();
		
		lan = new SelectTextArea();
		net = new SelectTextArea();
		age = new SelectTextArea();

		panel = new Panel();
		panel.border();
		
		panel.place(0, 0, 1, 1, 0, 0, 0, 0, Cell.wrap((new Label("LAN IP address")).label));
		panel.place(0, 1, 1, 1, 1, 0, 0, 0, Cell.wrap((new Label("Internet IP address")).label));
		panel.place(0, 2, 1, 1, 1, 0, 0, 0, Cell.wrap((new Label("Age of information")).label));
		
		panel.place(1, 0, 1, 1, 0, 1, 0, 0, Cell.wrap(lan).fillWide());
		panel.place(1, 1, 1, 1, 1, 1, 0, 0, Cell.wrap(net).fillWide());
		panel.place(1, 2, 1, 1, 1, 1, 0, 0, Cell.wrap(age).fillWide());
		
		panel.place(1, 3, 1, 1, 1, 1, 0, 0, Cell.wrap((new Button(refreshAction)).button).grow());
		
		

		// make these dialogs have a white background with the little font in light gray

		// Make our inner View object and connect the Model below to it
		view = new MyView();
		program.core.model.add(view); // When the Model below changes, it will call our view.refresh() method
		view.refresh();
		
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("pipe/icon.gif")));
		frame.setTitle("Information");
		frame.setBounds(Screen.positionSize(600, 200));
		frame.setContentPane(panel.panel);
	}
	
	private final Program program;
	public final JFrame frame;
	public final Panel panel;
	
	private final SelectTextArea lan;
	private final SelectTextArea net;
	private final SelectTextArea age;
	
	

	@Override public void close() {
		if (already()) return;

		frame.setVisible(false);
		frame.dispose(); // Dispose the frame so the process can close
	}
	
	
	
	

	private final RefreshAction refreshAction;
	private class RefreshAction extends AbstractAction {
		public RefreshAction() { super("Refresh"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {
			try {
				
				program.core.refreshHere();

			} catch (Exception e) { Mistake.stop(e); }
		}
	}
	
	
	
	// View

	// When our Model underneath changes, it calls these methods
	private final View view;
	private class MyView implements View {

		// The Model beneath changed, we need to update what we show the user
		public void refresh() {
			Refresh.can(refreshAction, program.core.model.canRefresh());
			Refresh.text(lan, program.core.model.lan());
			Refresh.text(net, program.core.model.net());
			Refresh.text(age, program.core.model.age());
		}

		// The Model beneath closed, take this View off the screen
		public void vanish() { close(me()); }
	}
	
	/** Give inner classes a link to this outer object. */
	private InfoFrame me() { return this; }
	
	
	
	
	
	
	
	
	
}
