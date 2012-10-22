package org.zootella.main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;

import net.roydesign.mac.MRJAdapter;

import org.zootella.base.desktop.Desktop;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.OldClose;
import org.zootella.base.user.Screen;
import org.zootella.base.user.panel.Cell;
import org.zootella.base.user.panel.Panel;
import org.zootella.demo.hash.HashDemo;

/** The main window on the screen that lists the running pipes. */
public class Window extends OldClose {

	// Object

	/** Make the program's main window on the screen. */
	public Window(User user) {
		program = user.program;
		
		snippetAction = new SnippetAction();
		browserAction = new BrowserAction();
		connectAction = new ConnectAction();
		hashAction = new HashAction();
		hereAction = new HereAction();
		spinAction = new SpinAction();
		trackerAction = new TrackerAction();

		//TODO code more of these and then enable their buttons
		browserAction.setEnabled(false);
		connectAction.setEnabled(false);
		spinAction.setEnabled(false);
		trackerAction.setEnabled(false);
		
		Panel buttons = Panel.row();
		buttons.add(Cell.wrap(new JButton(snippetAction)));
		buttons.add(Cell.wrap(new JButton(browserAction)));
		buttons.add(Cell.wrap(new JButton(connectAction)));
		buttons.add(Cell.wrap(new JButton(hashAction)));
		buttons.add(Cell.wrap(new JButton(hereAction)));
		buttons.add(Cell.wrap(new JButton(spinAction)));
		buttons.add(Cell.wrap(new JButton(trackerAction)));
		buttons.add(Cell.wrap(new JButton(user.exitAction)));
		
		panel = new Panel();
		panel.border();
		panel.place(0, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(buttons.panel).lowerLeft().grow());

		frame = new JFrame();
		frame.addWindowListener(new MyWindowListener()); // Find out when the user closes the window from the taskbar
		if (Desktop.isMac()) {
			MRJAdapter.addQuitApplicationListener(new MyQuitActionListener()); // And from the Mac application menu
			MRJAdapter.addReopenApplicationListener(new MyReopenActionListener()); // And when she clicks the dock icon
		}
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(Guide.icon)));
		frame.setTitle(Main.name);
		frame.setBounds(Screen.positionSize(new Dimension(670, 130)));
		frame.setContentPane(panel.panel);
	}

	public final Program program;

	public final JFrame frame;
	public final Panel panel;

	@Override public void close() {
		if (already()) return;
		
		frame.setVisible(false);
		frame.dispose(); // Dispose the frame so the process can close
	}

	private final SnippetAction snippetAction;
	private class SnippetAction extends AbstractAction {
		public SnippetAction() { super("Snippet"); }
		public void actionPerformed(ActionEvent a) {
			try {
				Snippet.snippet(program);
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	
	// Demos
	
	private final BrowserAction browserAction;
	private class BrowserAction extends AbstractAction {
		public BrowserAction() { super("Browser"); }
		public void actionPerformed(ActionEvent a) {
			try {
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	private final ConnectAction connectAction;
	private class ConnectAction extends AbstractAction {
		public ConnectAction() { super("Connect"); }
		public void actionPerformed(ActionEvent a) {
			try {
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	private final HashAction hashAction;
	private class HashAction extends AbstractAction {
		public HashAction() { super("Hash"); }
		public void actionPerformed(ActionEvent a) {
			try {
				
				new HashDemo();
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	private final HereAction hereAction;
	private class HereAction extends AbstractAction {
		public HereAction() { super("Here"); }
		public void actionPerformed(ActionEvent a) {
			try {
				
				program.user.here.frame.setVisible(true);
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	private final SpinAction spinAction;
	private class SpinAction extends AbstractAction {
		public SpinAction() { super("Spin"); }
		public void actionPerformed(ActionEvent a) {
			try {
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	private final TrackerAction trackerAction;
	private class TrackerAction extends AbstractAction {
		public TrackerAction() { super("Tracker"); }
		public void actionPerformed(ActionEvent a) {
			try {
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	

	
	
	/** On Windows, the user right-clicked the taskbar button and clicked "X Close" or keyed Alt+F4. */
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent w) {
			try {
				program.user.show(false);
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	/** On Mac, the user clicked the Quit menu item from the top left of the screen or from the program's icon on the dock. */
	private class MyQuitActionListener implements ActionListener {
		@Override public void actionPerformed(ActionEvent a) {
			try {
				program.user.exitAction.actionPerformed(a);
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	/** On Mac, the user clicked the program's icon on the dock. */
	private class MyReopenActionListener implements ActionListener {
		@Override public void actionPerformed(ActionEvent a) {
			try {
				program.user.restoreAction.actionPerformed(a);
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
}
