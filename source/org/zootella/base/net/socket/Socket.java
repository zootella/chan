package org.zootella.base.net.socket;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.zootella.base.data.Bin;
import org.zootella.base.net.name.IpPort;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.base.time.Duration;
import org.zootella.base.time.Now;

/** An open TCP socket connection. */
public class Socket extends Close {

	/** Make a Socket object to hold a TCP socket connection and information about how it connected. */
	public Socket(SocketChannel channel, IpPort ipPort, boolean outgoing, Now start) {
		this.channel = channel;
		this.ipPort = ipPort;
		this.outgoing = outgoing;
		connect = new Duration(start);
	}

	/** The Java SocketChannel object that is this TCP socket connection. */
	public final SocketChannel channel;
	/** The IP address and port number of the peer on the far end of this connection. */
	public final IpPort ipPort;
	/** true if we connected out to the peer, false if the peer connected in to us. */
	public final boolean outgoing;
	/** How long we took to connect, or connect.stop is when we accepted the connection. */
	public final Duration connect;

	/** Disconnect this TCP socket connection. */
	@Override public void close() {
		if (already()) return;
		try { channel.close(); } catch (Throwable t) { Mistake.log(t); }
	}

	/** Increase the socket buffer size if necessary. */
	public static void size(SocketChannel channel) throws IOException {
		if (channel.socket().getSendBufferSize() < Bin.medium) // Probably already 8137 bytes by default
			channel.socket().setSendBufferSize(Bin.medium);
		if (channel.socket().getReceiveBufferSize() < Bin.medium)
			channel.socket().setReceiveBufferSize(Bin.medium);
	}
}
