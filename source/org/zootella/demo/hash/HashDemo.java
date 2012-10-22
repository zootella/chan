package org.zootella.demo.hash;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.zootella.base.process.Mistake;
import org.zootella.base.state.OldClose;
import org.zootella.base.user.Dialog;
import org.zootella.base.user.Screen;
import org.zootella.base.user.panel.Cell;
import org.zootella.base.user.panel.Panel;
import org.zootella.base.user.widget.TextLine;
import org.zootella.main.Guide;

/** The Hash window that lets the user hash a file and shows progress. */
public class HashDemo extends OldClose {
	
	public HashDemo() {
		
		panel = new Panel();
		panel.border();
		
		//xywh tlbr
		panel.place(0, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("Path")));
		panel.place(1, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(path).fillWide());
		panel.place(2, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(new JButton(openAction)));

		panel.place(1, 1, 1, 1, 0, 0, 0, 0, Cell.wrap(status1.area).fillWide());
		panel.place(1, 2, 1, 1, 0, 0, 0, 0, Cell.wrap(status2.area).fillWide());
		panel.place(1, 3, 1, 1, 0, 0, 0, 0, Cell.wrap(status3.area).fillWide());
		
		
		
		Panel toolbar = new Panel();
		toolbar.place(0, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(new JButton(startAction)));
		toolbar.place(1, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(new JButton(stopAction)));
		toolbar.place(2, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(new JButton(resetAction)));
		panel.place(1, 4, 1, 1, 0, 0, 0, 0, Cell.wrap(toolbar.panel).lowerLeft().grow());
		
		

		frame = new JFrame();
		frame.addWindowListener(new MyWindowListener()); // Find out when the user closes the window from the taskbar
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(Guide.icon)));
		frame.setTitle("Hash");
		frame.setBounds(Screen.positionSize(Guide.sizeHashFrame));
		frame.setContentPane(panel.panel);
		
		frame.setVisible(true);
	}
	
	public final JFrame frame;
	public final Panel panel;
	
	private final JTextField path = new JTextField();
	private final TextLine status1 = new TextLine();
	private final TextLine status2 = new TextLine();
	private final TextLine status3 = new TextLine();

	@Override public void close() {
		if (already()) return;

		frame.setVisible(false);
		frame.dispose(); // Dispose the frame so the process can close
	}

	
	/** The user closed the window with the corner X, or by right-clicking the taskbar button. */
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent w) {
			try {
				close(HashDemo.this);
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	
	
	private final OpenAction openAction = new OpenAction();
	private class OpenAction extends AbstractAction {
		public OpenAction() { super("Open..."); } // Specify the button text
		public void actionPerformed(ActionEvent a) {
			try {

				Dialog.chooseFile(frame, path);

			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	private final StartAction startAction = new StartAction();
	private class StartAction extends AbstractAction {
		public StartAction() { super("Start"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {
			try {
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	private final StopAction stopAction = new StopAction();
	private class StopAction extends AbstractAction {
		public StopAction() { super("Stop"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {
			try {
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	private final ResetAction resetAction = new ResetAction();
	private class ResetAction extends AbstractAction {
		public ResetAction() { super("Reset"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {
			try {
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
}
