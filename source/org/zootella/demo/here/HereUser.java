package org.zootella.demo.here;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.base.user.Refresh;
import org.zootella.base.user.Screen;
import org.zootella.base.user.panel.Cell;
import org.zootella.base.user.panel.Panel;
import org.zootella.base.user.widget.TextLine;
import org.zootella.main.Guide;
import org.zootella.main.Program;
import org.zootella.main.User;

/** The Info window that shows advanced statistics and diagnostic information. */
public class HereUser extends Close {
	
	private final HereCore core;

	public HereUser(HereCore core) {
		this.core = core;

		panel = new Panel();
		panel.border();
		
		//          0, 0
		panel.place(0, 1, 1, 1, 0, 0, 1, 2, Cell.wrap(new JLabel("LAN")));
		panel.place(0, 2, 1, 1, 0, 0, 1, 2, Cell.wrap(new JLabel("Bind")));
		panel.place(0, 3, 1, 1, 0, 0, 0, 2, Cell.wrap(new JLabel("NAT")));
		//          0, 4
		//          0, 5
		//          0, 6
		panel.place(0, 7, 1, 1, 0, 0, 1, 2, Cell.wrap(new JLabel("Center")));
		
		panel.place(1, 0, 4, 1, 0, 0, 1, 2, Cell.wrap(summary.area));
		panel.place(1, 1, 1, 1, 0, 0, 1, 2, Cell.wrap(lanValue.area));
		panel.place(1, 2, 1, 1, 0, 0, 1, 2, Cell.wrap(bindValue.area));
		panel.place(1, 3, 1, 1, 0, 0, 0, 2, Cell.wrap(natModelValue.area));
		panel.place(1, 4, 1, 1, 0, 0, 0, 2, Cell.wrap(natIpValue.area));
		panel.place(1, 5, 1, 1, 0, 0, 0, 2, Cell.wrap(natTcpValue.area));
		panel.place(1, 6, 1, 1, 0, 0, 1, 2, Cell.wrap(natUdpValue.area));
		panel.place(1, 7, 1, 1, 0, 0, 1, 2, Cell.wrap(centerValue.area));

		//          2, 0
		panel.place(2, 1, 1, 1, 0, 0, 1, 2, Cell.wrap(lanTime.area));
		panel.place(2, 2, 1, 1, 0, 0, 1, 2, Cell.wrap(bindTime.area));
		panel.place(2, 3, 1, 1, 0, 0, 0, 2, Cell.wrap(natModelTime.area));
		panel.place(2, 4, 1, 1, 0, 0, 0, 2, Cell.wrap(natIpTime.area));
		panel.place(2, 5, 1, 1, 0, 0, 0, 2, Cell.wrap(natTcpTime.area));
		panel.place(2, 6, 1, 1, 0, 0, 1, 2, Cell.wrap(natUdpTime.area));
		panel.place(2, 7, 1, 1, 0, 0, 1, 2, Cell.wrap(centerTime.area));
		
		//          3, 0
		panel.place(3, 1, 1, 1, 0, 0, 1, 2, Cell.wrap(lanError.area));
		panel.place(3, 2, 1, 1, 0, 0, 1, 2, Cell.wrap(bindError.area));
		panel.place(3, 3, 1, 1, 0, 0, 0, 2, Cell.wrap(natModelError.area));
		panel.place(3, 4, 1, 1, 0, 0, 0, 2, Cell.wrap(natIpError.area));
		panel.place(3, 5, 1, 1, 0, 0, 0, 2, Cell.wrap(natTcpError.area));
		panel.place(3, 6, 1, 1, 0, 0, 1, 2, Cell.wrap(natUdpError.area));
		panel.place(3, 7, 1, 1, 0, 0, 1, 2, Cell.wrap(centerError.area));

		//          4, 0
		panel.place(4, 1, 1, 1, 0, 0, 1, 2, Cell.wrap(new JButton(lanAction)));
		panel.place(4, 2, 1, 1, 0, 0, 1, 2, Cell.wrap(new JButton(bindAction)));
		panel.place(4, 3, 1, 4, 0, 0, 0, 2, Cell.wrap(new JButton(natAction)));
		//          4, 4
		//          4, 5
		//          4, 6
		panel.place(4, 7, 1, 1, 0, 0, 1, 2, Cell.wrap(new JButton(centerAction)).grow());

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(Guide.icon)));
		frame.setTitle("Information");
		frame.setBounds(Screen.positionSize(Guide.sizeInfoFrame));
		frame.setContentPane(panel.panel);
	}
	
	public final JFrame frame;
	public final Panel panel;
	
	private final TextLine summary = new TextLine();
	
	private final TextLine lanValue = new TextLine();
	private final TextLine bindValue = new TextLine();
	private final TextLine natModelValue = new TextLine();
	private final TextLine natIpValue = new TextLine();
	private final TextLine natTcpValue = new TextLine();
	private final TextLine natUdpValue = new TextLine();
	private final TextLine centerValue = new TextLine();
	
	private final TextLine lanTime = new TextLine();
	private final TextLine bindTime = new TextLine();
	private final TextLine natModelTime = new TextLine();
	private final TextLine natIpTime = new TextLine();
	private final TextLine natTcpTime = new TextLine();
	private final TextLine natUdpTime = new TextLine();
	private final TextLine centerTime = new TextLine();
	
	private final TextLine lanError = new TextLine();
	private final TextLine bindError = new TextLine();
	private final TextLine natModelError = new TextLine();
	private final TextLine natIpError = new TextLine();
	private final TextLine natTcpError = new TextLine();
	private final TextLine natUdpError = new TextLine();
	private final TextLine centerError = new TextLine();

	@Override public void close() {
		if (already()) return;

		frame.setVisible(false);
		frame.dispose(); // Dispose the frame so the process can close
	}

	private final LanAction lanAction = new LanAction();
	private class LanAction extends AbstractAction {
		public LanAction() { super("Refresh"); } // Specify the button text
		public void actionPerformed(ActionEvent a) {
			try {
				core.refreshLan();
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	private final BindAction bindAction = new BindAction();
	private class BindAction extends AbstractAction {
		public BindAction() { super("Refresh"); }
		public void actionPerformed(ActionEvent a) {
			try {
				core.refreshBind();
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	private final NatAction natAction = new NatAction();
	private class NatAction extends AbstractAction {
		public NatAction() { super("Refresh"); }
		public void actionPerformed(ActionEvent a) {
			try {
				core.refreshNat();
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}

	private final CenterAction centerAction = new CenterAction();
	private class CenterAction extends AbstractAction {
		public CenterAction() { super("Refresh"); }
		public void actionPerformed(ActionEvent a) {
			try {
				core.refreshCenter();
			} catch (Throwable t) { Mistake.stop(t); }
		}
	}
	
	
	// View
	
	@Override public void pulseScreen() {
		if (!frame.isVisible()) return;
		
		Refresh.text(lanValue.area, core.userLanIp());
		Refresh.text(bindValue.area, core.userBindPort());
		Refresh.text(natModelValue.area, core.userNatModel());
		Refresh.text(natIpValue.area, core.userNatIp());
		Refresh.text(natTcpValue.area, core.userMapTcp());
		Refresh.text(natUdpValue.area, core.userMapUdp());
		Refresh.text(centerValue.area, core.userCenterIp());
		
		Refresh.text(lanTime.area, core.userLanIpTime());
		Refresh.text(bindTime.area, core.userBindPortTime());
		Refresh.text(natModelTime.area, core.userNatModelTime());
		Refresh.text(natIpTime.area, core.userNatIpTime());
		Refresh.text(natTcpTime.area, core.userMapTcpTime());
		Refresh.text(natUdpTime.area, core.userMapUdpTime());
		Refresh.text(centerTime.area, core.userCenterIpTime());
		
		Refresh.text(lanError.area, core.userLanIpError());
		Refresh.text(bindError.area, core.userBindPortError());
		Refresh.text(natModelError.area, core.userNatModelError());
		Refresh.text(natIpError.area, core.userNatIpError());
		Refresh.text(natTcpError.area, core.userMapTcpError());
		Refresh.text(natUdpError.area, core.userMapUdpError());
		Refresh.text(centerError.area, core.userCenterIpError());
	}
}
