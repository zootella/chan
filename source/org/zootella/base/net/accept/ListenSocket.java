package org.zootella.base.net.accept;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

import org.zootella.base.exception.NetException;
import org.zootella.base.net.name.Port;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.OldClose;

/** A TCP server socket bound to port that can listen for a new incoming connection. */
public class ListenSocket extends OldClose {

	// Open

	/** Bind a new TCP server socket to port. */
	public ListenSocket(Port port) {
		try {
			this.port = port;
			channel = ServerSocketChannel.open();
			channel.socket().bind(new InetSocketAddress(port.port));
		} catch (IOException e) { throw new NetException(e); }
	}

	// Look

	/** The port number this socket is bound to. */
	public final Port port;
	/** The Java ServerSocketChannel object that is this TCP server socket. */
	public final ServerSocketChannel channel;
	
	// Close

	/** Stop listening on port. */
	@Override public void close() {
		if (already()) return;
		try { channel.close(); } catch (Throwable t) { Mistake.log(t); } // May throw IOException, whatever it throws, we must keep going
	}
}
