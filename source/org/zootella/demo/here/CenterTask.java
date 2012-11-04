package org.zootella.demo.here;

import org.zootella.base.data.Number;
import org.zootella.base.data.Outline;
import org.zootella.base.data.Text;
import org.zootella.base.exception.DataException;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.net.name.IpPort;
import org.zootella.base.net.name.Port;
import org.zootella.base.net.packet.Packet;
import org.zootella.base.net.packet.PacketReceive;
import org.zootella.base.net.packet.Packets;
import org.zootella.base.net.web.DomainTask;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.base.state.Once;
import org.zootella.base.state.Receive;
import org.zootella.base.state.Result;
import org.zootella.base.state.Update;
import org.zootella.base.time.Duration;
import org.zootella.base.time.Egg;
import org.zootella.center.Center;

/** A CenterTask figures out what our IP address is once and right now. */
public class CenterTask extends Close {
	
	// Make
	
	public CenterTask(Update up, Packets packets) {

		// Save and connect the given object that sends UDP packets
		this.packets = packets;
		packetReceive = new MyPacketReceive();
		packets.add(packetReceive);

		// Save and connect our Update objects
		this.up = up;
		egg = new Egg();
		sent = new Once();
		update = new Update();
		update.send();
	}

	private final Update up;
	private final Update update;
	private final Packets packets;
	private final Egg egg;
	private final Once sent;
	
	private DomainTask domain;
	private IpPort center;

	@Override public void close() {
		if (already()) return;
		
		packets.remove(packetReceive);
		close(egg);
		close(domain);
	}

	// Result
	
	public Result<IpPort> result() {
		return new Result<IpPort>(internet, new Duration(egg.start), exception);//TODO use whenClosed() here
	}

	private ProgramException exception;
	private IpPort internet;

	// Do

	@Override public void pulse() {
		try {

			// Throw a TimeException if we've been trying to finish for more than 4 seconds
			egg.check();

			// Look up the IP address of the central server
			if (no(domain))
				domain = new DomainTask(update, Text.before(Center.site, ":"));
			if (done(domain) && center == null)
				center = new IpPort(domain.result(), new Port(Number.toInt(Text.after(Center.site, ":"))));

			// Send the central server a UDP packet to find out what our IP address is
			if (center != null && internet == null && sent.once())
				packets.send((new Outline("aq")).toData(), center);

		} catch (ProgramException e) { exception = e; close(this); up.send(); }
	}
	
	private final MyPacketReceive packetReceive;
	private class MyPacketReceive implements PacketReceive {
		public boolean receive(Packet packet) {
			if (closed()) return false;
			try {
				
				// Look for the packet response
				Outline o = new Outline(packet.bin.data()); // Parse the UDP payload into an Outline
				if (o.name.equals("ap")) { // Address response
					if (o.has("hash") && !o.value("hash").equals(o.value().hash())) // Hash check
						throw new DataException("received corrupted ap");
					internet = new IpPort(o.value()); // Read
					close(CenterTask.this); // It worked, we're done
					up.send();
					return true;
				}
			}
			catch (DataException e) { Mistake.log(e); }
			catch (ProgramException e) { exception = e; close(CenterTask.this); up.send(); }
			return false;
		}
	}
}
