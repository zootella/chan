package org.zootella.base.user.widget;

import java.awt.Font;

import javax.swing.JTextArea;

import org.zootella.base.user.Face;

/** A wrapping, read-only text area that lets the user select and copy. */
public class TextValue {
	
	public TextValue() {
		this(Face.font(), "");
	}
	
	public TextValue(String s) {
		this(Face.font(), s);
	}
	
	public TextValue(Font font) {
		this(font, "");
	}

	public TextValue(Font font, String s) {
		area = new JTextArea(s);
		area.setLineWrap(true);
		area.setOpaque(false);
		area.setBorder(null);
		area.setEditable(false);
		area.setFont(font);
		new TextMenu(area);
	}
	
	public final JTextArea area;
}
