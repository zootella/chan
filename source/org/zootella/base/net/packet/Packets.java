package org.zootella.base.net.packet;

import java.util.ArrayList;
import java.util.List;

import org.zootella.base.data.Bin;
import org.zootella.base.data.Data;
import org.zootella.base.list.BinBin;
import org.zootella.base.net.name.IpPort;
import org.zootella.base.net.name.Port;
import org.zootella.base.state.Close;

/** The program's Packets object listens on a port to send and receive UDP packets. */
public class Packets extends Close {

	/** Make a new Packets object that listens on the given port number. */
	public Packets(Port port) {

		// Make inside
		packets = new ArrayList<Packet>();
		receivers = new ArrayList<PacketReceive>();
		bins = new BinBin();
		listen = new ListenPacket(port);
	}
	
	/** Packets we're about to send. */
	private final List<Packet> packets;
	/** A List of PacketReceive objects above that we show each packet we receive. */
	private final List<PacketReceive> receivers;
	/** A stack of empty bins to recycle. */
	private final BinBin bins;
	/** Our datagram socket bound to port that we use to send and receive UDP packets. */
	private final ListenPacket listen;
	/** Our Update object that objects below tell when they've changed. */
//	private final Update update;

	/** A SendTask that sends a UDP packet. */
	private SendTask send;
	/** A ReceiveTask that waits for a UDP packet to arrive, and then receives it. */
	private ReceiveTask receive;

	@Override public void close() {
		if (already()) return;
		close(listen);
		close(send);
		close(receive);
		receivers.clear(); // Stop bothering objects above
	}

	@Override public void pulse() {

		// Send
		if (done(send)) { // Our SendTask finished sending a packet
			Bin bin = send.result();
			send = null;
			bins.add(bin); // Recycle the Bin it used
		}
		if (no(send) && !packets.isEmpty()) // We're not sending a packet right now and we've got one to send
			send = new SendTask(listen, packets.remove(0)); // Send it
		
		// Receive
		if (done(receive)) { // Our ReceiveTask finished waiting for and getting a packet
			Packet packet = receive.result(); // Get the packet
			receive = null;
			for (PacketReceive r : new ArrayList<PacketReceive>(receivers)) // Show it to each interested object above
				if (r.receive(packet))
					break; // r recognized it, don't show it to the other PacketReceive objects above
			bins.add(packet.bin); // That's it for packet, recycle its Bin
		}
		if (no(receive)) // Wait for the next packet to arrive
			receive = new ReceiveTask(listen, bins.get());
	}
	
	// Send

	/** Send data to ipPort as the payload of a UDP packet. */
	public void send(Data data, IpPort ipPort) {
		confirmOpen();
		Bin bin = bin();
		bin.add(data.clip());
		send(bin, ipPort);
	}
	
	/** Get an empty Bin to fill with data and then send. */
	public Bin bin() {
		confirmOpen();
		return bins.get();
	}

	/** Send the data in bin to ipPort as a new UDP packet. */
	public void send(Bin bin, IpPort ipPort) {
		confirmOpen();
		packets.add(new Packet(bin, ipPort));
		soon();
	}
	
	// Receive

	/** Add o to the list of objects this Packets object shows the packets it receives. */
	public void add(PacketReceive o) {
		confirmOpen(); // If this object is closed, we can't let it change, throw an exception
		if (!receivers.contains(o))
			receivers.add(o);
	}
	
	/** Remove o from the list of objects this Packets object bothers with arrived packets. */
	public void remove(PacketReceive o) {
		if (closed()) return; // If this object is closed, we can't let it change, do nothing
		receivers.remove(o);
	}
}
