package base.user.widget;

import javax.swing.JTextArea;

import base.user.Fonts;

/** A wrapping, read-only text area that lets the user select and copy. */
public class TextValue {
	
	public TextValue() {
		this("");
	}

	public TextValue(String s) {
		area = new JTextArea(s);
		area.setLineWrap(true);
		area.setOpaque(false);
		area.setBorder(null);
		area.setEditable(false);
		area.setFont(Fonts.platform());
		new TextMenu(area);
	}
	
	public final JTextArea area;
}
