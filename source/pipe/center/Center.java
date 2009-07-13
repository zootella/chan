package pipe.center;

import javax.swing.SwingUtilities;

import base.data.Bin;
import base.data.Data;
import base.data.Number;
import base.data.Outline;
import base.data.Text;
import base.encode.Hash;
import base.internet.name.Port;
import base.internet.packet.Packet;
import base.internet.packet.Packets;
import base.internet.packet.PacketReceive;
import base.process.Alive;
import base.process.Mistake;
import base.state.Close;

public class Center extends Close {

	/** Domain name and port number of the central server. */
	public static final String site = "bootcloud.info:9193";

	public static void main(String[] arguments) {
		SwingUtilities.invokeLater(new Runnable() { // Have the normal Swing thread call this run() method
			public void run() {
				try {
					new Center(); // Make and start the program
				} catch (Exception e) { Mistake.grab(e); } // Exception starting up
			}
		});
	}
	
	public Center() {
		packetMachine = new Packets(new Port(Number.toInt(Text.after(site, ":"))));
		packetMachine.add(new MyPacketReceive());
		Alive.still();
	}

	public final Packets packetMachine;

	@Override public void close() {
		if (already()) return;

		close(packetMachine);
		
		Mistake.close();
	}
	
	private class MyPacketReceive implements PacketReceive {
		public void receive(Packet packet) {
			if (closed()) return;
			try {
				
				// Receive packets and send responses
				Outline q = new Outline(packet.bin.data()); // Parse the UDP payload into an Outline
				if (q.name.equals("aq")) { // Address request
					
					Data data = packet.move.ipPort.data();
					Outline p = new Outline("ap", data); // Address response
					p.add("hash", Hash.hash(data)); // Optional integrity check

					Bin bin = packetMachine.bin();
					bin.add(p.toData());
					packetMachine.send(bin, packet.move.ipPort);
				}

			} catch (Exception e) { Mistake.ignore(e); } // Log and drop unknown packets
		}
	}
}