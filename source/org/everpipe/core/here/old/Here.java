package org.everpipe.core.here.old;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.zootella.exception.PlatformException;
import org.zootella.exception.ProgramException;
import org.zootella.net.name.Ip;
import org.zootella.net.name.IpPort;
import org.zootella.net.name.Port;
import org.zootella.net.packet.Packets;
import org.zootella.net.upnp.Router;
import org.zootella.net.upnp.name.Map;
import org.zootella.state.Close;
import org.zootella.state.Model;
import org.zootella.state.Receive;
import org.zootella.state.Update;
import org.zootella.time.Now;
import org.zootella.user.Describe;

public class Here extends Close {
	
	// Make
	
	public Here(Port port, Packets packets) {
		this.port = port;
		this.packets = packets;

		update = new Update(new MyReceive());
		refresh();
		
		model = new MyModel();
		model.pulse();
		model.changed();
		
		IpPort l = new IpPort(lan(), port);
		Map t = new Map(port, l, "TCP", "Pipe");
		Map u = new Map(port, l, "UDP", "Pipe");
		
		upnp = new Router(update, t, u);
	}
	
	private final Port port;
	private final Packets packets;
	private final Update update;
	
	private HereTask task; // The HereTask we used most recently to find internet at age
	private ProgramException exception; // The most recent exception from task
	private IpPort internet; // The most recent good Internet IP address we found for ourselves
	private Now age; // When we found internet
	
	private final Router upnp;

	@Override public void close() {
		if (already()) return;
		close(upnp);
		close(task);
		close(model);
	}

	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {
				
				if (done(task) && task.result.once()) {
					internet = task.internet();
					age = task.age();
					model.changed();
				}

			} catch (ProgramException e) { exception = e; }
		}
	}
	
	// Do
	
	/** Send a UDP packet to the central server to find out what our Internet IP address is. */
	public void refresh() {
		
		close(task);
		task = new HereTask(update, port, packets);
		
	}

	// Look

	/** Our internal IP address and listening port number on the LAN right now. */
	public IpPort lanIpPort() {
		return new IpPort(lan(), port);
	}
	
	public static Ip lan() {
		try {
			return new Ip(InetAddress.getLocalHost());
		} catch (UnknownHostException e) { throw new PlatformException(e); }
	}

	/** The most recent ProgramException that prevented us from finding out our Internet IP address. */
	public ProgramException exception() { return exception; }
	/** The most recent valid Internet IP address we've determined we have, null if we don't know yet. */
	public IpPort internet() { return internet; }
	/** When we found internet(). */
	public Now age() { return age; }
	
	// Model

	public final MyModel model;
	public class MyModel extends Model {

		public String lan()      { return Describe.object((Here.this.lanIpPort())); }
		public String internet() { return Describe.object((Here.this.internet())); }
		
		public String age() {
			if (Here.this.age() == null) return "";
			return Here.this.age().toString() + " (" + Describe.timeCoarse(Here.this.age().age()) + " ago)";
		}
		
		public String exception() { return Describe.object((Here.this.exception())); }
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
