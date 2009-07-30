package base.net.accept;

import base.net.flow.SocketBay;

/** Make an AcceptReceive in your object to have a Packets object show you UDP packets that arrive. */
public interface AcceptReceive {

	/** When we accept a new incoming socket connection, the Sockets object will call your receive() method, showing it to you. */
	public void receive(SocketBay socket);
}
