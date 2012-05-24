package org.zootella.pipe.user;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.zootella.base.desktop.Clipboard;
import org.zootella.base.process.Mistake;
import org.zootella.base.user.Screen;
import org.zootella.base.user.panel.Cell;
import org.zootella.base.user.panel.Panel;
import org.zootella.base.user.widget.TextArea;
import org.zootella.main.Main;
import org.zootella.main.Program;
import org.zootella.pipe.core.museum.Pipe;

public class ExchangeDialog {
	
	// Object

	public ExchangeDialog(Program program, Pipe pipe) {
		this.program = program;
		this.pipe = pipe;

		home = new TextArea();
		away = new TextArea();
		home.area.setEditable(false);
		home.area.setLineWrap(true);
		away.area.setLineWrap(true);
		JScrollPane homeScroll = new JScrollPane(home.area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane awayScroll = new JScrollPane(away.area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		Panel row = Panel.row();
		row.add(Cell.wrap(new JButton(new OkAction())));//TODO still using JButton, i see?
		row.add(Cell.wrap(new JButton(new CancelAction())));
		
		Panel panel = (new Panel()).border();
		panel.place(0, 0, 1, 1, 0, 0, 0, 1, Cell.wrap(new JLabel("Tell this code to your friend:"))); //Give this code to your friend:")));
		panel.place(1, 0, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("Enter your friend's code here:")));
		panel.place(0, 1, 1, 1, 1, 0, 0, 1, Cell.wrap(homeScroll).fill());
		panel.place(1, 1, 1, 1, 1, 0, 0, 0, Cell.wrap(awayScroll).fill());
		panel.place(0, 2, 1, 1, 1, 0, 0, 1, Cell.wrap(new JButton(new CopyAction())));
		panel.place(1, 2, 1, 1, 1, 0, 0, 0, Cell.wrap(new JButton(new PasteAction())));
		panel.place(0, 3, 2, 1, 1, 0, 0, 0, Cell.wrap(row.panel).lowerRight());
		
		home.area.setText(pipe.homeCode());

		dialog = new JDialog(program.user.main.frame, "Code Exchange", true); // true to make a modal dialog
		dialog.setContentPane(panel.panel);
		dialog.setBounds(Screen.positionSize(Guide.sizeExchangeDialog)); // Set the dialog size and pick a random location
		dialog.setVisible(true); // Show the dialog box on the screen
	}
	
	private final Program program;
	private final Pipe pipe;
	private final JDialog dialog;
	private final TextArea home;
	private final TextArea away;
	
	// Action

	private class CopyAction extends AbstractAction {
		public CopyAction() { super("Copy"); }
		public void actionPerformed(ActionEvent a) {
			try {
				
				Clipboard.copy(home.area.getText());
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	
	private class PasteAction extends AbstractAction {
		public PasteAction() { super("Paste"); }
		public void actionPerformed(ActionEvent a) {
			try {
				
				away.area.setText(Clipboard.paste());
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	
	private class OkAction extends AbstractAction {
		public OkAction() { super("OK"); }
		public void actionPerformed(ActionEvent a) {
			try {
				
				pipe.awayCode(away.area.getText());
				if (!pipe.hasAwayCode())
					JOptionPane.showMessageDialog(
						program.user.main.frame,
						"Unable to parse code. Make sure you pasted it correctly, and try again.",
						Main.name,
						JOptionPane.WARNING_MESSAGE);
				else
					dialog.dispose();

				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	
	private class CancelAction extends AbstractAction {
		public CancelAction() { super("Cancel"); }
		public void actionPerformed(ActionEvent a) {
			try {
				
				dialog.dispose();
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
}
