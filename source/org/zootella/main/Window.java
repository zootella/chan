package org.zootella.main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.roydesign.mac.MRJAdapter;

import org.zootella.base.desktop.Desktop;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.OldClose;
import org.zootella.base.user.Screen;

/** The main window on the screen that lists the running pipes. */
public class Window extends OldClose {

	// Object

	/** Make the program's main window on the screen. */
	public Window(User user) {
		program = user.program;
		
		Dimension d = new Dimension(Guide.pipeWidth, Guide.toolHeight);
		
		//frame
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setLayout(null);
		frame.setSize(d);
		frame.addWindowListener(new MyWindowListener()); // Find out when the user closes the window from the taskbar
		if (Desktop.isMac()) {
			MRJAdapter.addQuitApplicationListener(new MyQuitActionListener()); // And from the Mac application menu
			MRJAdapter.addReopenApplicationListener(new MyReopenActionListener()); // And when she clicks the dock icon
		}
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(Guide.icon)));
		frame.setTitle(Main.name);
		frame.setBounds(Screen.positionSize(frame.getSize().width, frame.getSize().height));
		
		//tool
		panel = new WindowPanel(user, this);
		
		//together
		frame.setContentPane(panel.panel);

		
		
		
		
		
	}

	public final Program program;

	public final JFrame frame;
	public final WindowPanel panel;

	@Override public void close() {
		if (already()) return;
		
		frame.setVisible(false);
		frame.dispose(); // Dispose the frame so the process can close
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
