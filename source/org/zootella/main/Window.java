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
import javax.swing.JMenuItem;

import net.roydesign.mac.MRJAdapter;

import org.zootella.base.desktop.Desktop;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.base.user.Screen;
import org.zootella.base.user.panel.Cell;
import org.zootella.base.user.panel.Panel;

/** The main window on the screen that lists the running pipes. */
public class Window extends Close {

	// Object

	/** Make the program's main window on the screen. */
	public Window(User user) {
		program = user.program;
		
		snippetAction = new SnippetAction();
		preferencesAction = new PreferencesAction();
		informationAction = new InformationAction();
		hashAction = new HashAction();
		aboutAction = new AboutAction();
		
		panel = new Panel();
		panel.border();
		panel.place(0, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(new JButton(snippetAction)).lowerLeft());
		panel.place(1, 0, 1, 1, 0, 1, 0, 0, Cell.wrap(new JButton(preferencesAction)).lowerLeft());
		panel.place(2, 0, 1, 1, 0, 1, 0, 0, Cell.wrap(new JButton(informationAction)).lowerLeft());
		panel.place(3, 0, 1, 1, 0, 1, 0, 0, Cell.wrap(new JButton(hashAction)).lowerLeft());
		panel.place(4, 0, 1, 1, 0, 1, 0, 0, Cell.wrap(new JButton(aboutAction)).lowerLeft());
		panel.place(5, 0, 1, 1, 0, 1, 0, 0, Cell.wrap(new JButton(user.exitAction)).lowerLeft().grow());

		frame = new JFrame();
		frame.addWindowListener(new MyWindowListener()); // Find out when the user closes the window from the taskbar
		if (Desktop.isMac()) {
			MRJAdapter.addQuitApplicationListener(new MyQuitActionListener()); // And from the Mac application menu
			MRJAdapter.addReopenApplicationListener(new MyReopenActionListener()); // And when she clicks the dock icon
		}
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(Guide.icon)));
		frame.setTitle(Main.name);
		frame.setBounds(Screen.positionSize(new Dimension(600, 130)));
		frame.setContentPane(panel.panel);
	}

	public final Program program;

	public final Panel panel;
	public final JFrame frame;

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

	private final PreferencesAction preferencesAction;
	private class PreferencesAction extends AbstractAction {
		public PreferencesAction() { super("Preferences"); }
		public void actionPerformed(ActionEvent a) {
			try {
				
				System.out.println("preferences action");

			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	private final InformationAction informationAction;
	private class InformationAction extends AbstractAction {
		public InformationAction() { super("Information"); }
		public void actionPerformed(ActionEvent a) {
			try {

				program.user.info.frame.setVisible(true);
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	private final HashAction hashAction;
	private class HashAction extends AbstractAction {
		public HashAction() { super("Hash"); }
		public void actionPerformed(ActionEvent a) {
			try {
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	private final AboutAction aboutAction;
	private class AboutAction extends AbstractAction {
		public AboutAction() { super("About"); }
		public void actionPerformed(ActionEvent a) {
			try {
				
				System.out.println("about action");
				
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
