package org.zootella.base.user.widget;

import javax.swing.JTextArea;

import org.zootella.base.user.Face;

public class TextArea {
	
	public TextArea() {
		area = new JTextArea();
		area.setFont(Face.font());
		new TextMenu(area);
	}
	
	public final JTextArea area;
}
