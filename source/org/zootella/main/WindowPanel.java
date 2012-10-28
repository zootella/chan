package org.zootella.main;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.base.user.skin.PlainButton;
import org.zootella.base.user.widget.Grip;
import org.zootella.demo.hash.HashDemo;

public class WindowPanel {
	
	// Object
	
	public WindowPanel(User user, Window window) {
		this.program = window.program;

		closeAction = new CloseAction();
		makeAction = new MakeAction();
		menuAction = new MenuAction();
		
		snippetAction = new SnippetAction();
		browserAction = new BrowserAction();
		connectAction = new ConnectAction();
		hashAction = new HashAction();
		hereAction = new HereAction();
		spinAction = new SpinAction();
		trackerAction = new TrackerAction();
		
		browserAction.setEnabled(false);
		connectAction.setEnabled(false);
		spinAction.setEnabled(false);
		
		menu = new JPopupMenu();
		menu.add(new JMenuItem(snippetAction));
		menu.addSeparator();
		menu.add(new JMenuItem(browserAction));
		menu.add(new JMenuItem(connectAction));
		menu.add(new JMenuItem(hashAction));
		menu.add(new JMenuItem(hereAction));
		menu.add(new JMenuItem(spinAction));
		menu.add(new JMenuItem(trackerAction));
		menu.addSeparator();
		menu.add(new JMenuItem(user.exitAction));
		
		PlainButton closeButton = new PlainButton(closeAction, user.skin, Guide.skinToolClose, Guide.toolClose, Guide.ink);
		PlainButton makeButton = new PlainButton(makeAction, user.skin, Guide.skinToolMake, Guide.toolMake, Guide.ink);
		PlainButton menuButton = new PlainButton(menuAction, user.skin, Guide.skinToolMenu, Guide.toolMenu, Guide.ink);
		
		panel = new JPanel();
		panel.setLayout(null);
		panel.setSize(Guide.sizeTool);
		panel.setBackground(new Color(0xebebeb));
		
		panel.add(closeButton.button);
		panel.add(makeButton.button);
		panel.add(menuButton.button);
		new Grip(window.frame, panel);
	}
	
	private final Program program;
	private final JPopupMenu menu;

	public final JPanel panel;

	// Action

	private final CloseAction closeAction;
	private class CloseAction extends AbstractAction {
		public CloseAction() { super("x"); }
		public void actionPerformed(ActionEvent a) {
			try {

				program.user.show(false);
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	private final MakeAction makeAction;
	private class MakeAction extends AbstractAction {
		public MakeAction() { super("New"); }
		public void actionPerformed(ActionEvent a) {
			try {

			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	private final MenuAction menuAction;
	private class MenuAction extends AbstractAction {
		public MenuAction() { super("v"); }
		public void actionPerformed(ActionEvent a) {
			try {
				
				menu.show(panel, Guide.toolMenu.x, Guide.toolMenu.y + Guide.toolMenu.height);
				
			} catch (Throwable t) { Mistake.stop(t); }
		}
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
	
	// Demo

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

				Close.log("tracker action");

			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
}
