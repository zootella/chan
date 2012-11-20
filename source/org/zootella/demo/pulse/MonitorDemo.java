package org.zootella.demo.pulse;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.zootella.base.process.Mistake;
import org.zootella.base.pulse.Pulse;
import org.zootella.base.state.Close;
import org.zootella.base.user.Face;
import org.zootella.base.user.Screen;
import org.zootella.base.user.panel.Cell;
import org.zootella.base.user.panel.Panel;
import org.zootella.base.user.widget.TextValue;
import org.zootella.main.Guide;

public class MonitorDemo extends Close {

	public MonitorDemo() {
		
		panel.border();
		panel.add(Cell.wrap(value.area).fill());
		
		frame.addWindowListener(new MyWindowListener());
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(Guide.icon)));
		frame.setTitle("Monitor");
		frame.setBounds(Screen.positionSize(new Dimension(450, 200)));
		frame.setContentPane(panel.panel);
		
		frame.setVisible(true);
		
		value.area.setText("hello world, the font size is something");
	}
	
	public final JFrame frame = new JFrame();
	public final Panel panel = new Panel();
	private final TextValue value = new TextValue(Face.fixed());

	@Override public void close() {
		if (already()) return;

		frame.setVisible(false);
		frame.dispose(); // Dispose the frame so the process can close
	}
	
	@Override public void pulseUser() {
		
		value.area.setText(Pulse.pulse.monitor.describeEfficiency() + "\r\n");
	}

	
	/** The user closed the window with the corner X, or by right-clicking the taskbar button. */
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent w) {
			try {
				close(MonitorDemo.this);
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	
	
	
	
	
}
