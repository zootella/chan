package base.user.skin;

import java.awt.Rectangle;

import javax.swing.Action;
import javax.swing.JButton;

import pipe.user.skin.Guide;

import base.user.Fonts;

// A button that always stays in the same place to be skinned
public class SkinButton {

	public SkinButton(Action action, Skin skin, ButtonGuide guide, Rectangle place) {
		button = new JButton(action);
		button.setLayout(null);
		button.setBounds(place);
		button.setFont(Fonts.program());
		
		button.setForeground(skin.color(Guide.skinInk));
		
		//TODO pngs for ghost, normal, float, and press
	}
	
	public final JButton button;
}
