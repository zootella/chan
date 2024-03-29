package org.zootella.base.net.connect;

import org.zootella.base.data.Data;
import org.zootella.base.data.Outline;
import org.zootella.base.exception.ChopException;
import org.zootella.base.exception.DataException;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.net.flow.SocketBay;
import org.zootella.base.net.name.IpPort;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.base.time.Egg;

public class Connect extends Close {
	
	/** Make a new TCP socket connection to ipPort, say hello and get hash response, in 4 seconds or less. */
	public Connect(IpPort ipPort, Data hello, Data hash) {
		this.ipPort = ipPort;
		this.hello = hello;
		this.hash = hash;
		
		egg = new Egg();
	}
	
	private final IpPort ipPort;
	private final Data hello;
	private final Data hash;
	private final Egg egg;
	private ConnectTask connect;

	@Override public void close() {
		if (already()) return;
		close(connect);
		if (exception != null)
			close(socket);
	}
	
	public SocketBay result() { check(exception, socket); return socket; }
	private ProgramException exception;
	private SocketBay socket;

	@Override public void pulse() {
		try {
			egg.check();
			
			// Connect and upload hello
			if (no(connect))
				connect = new ConnectTask(ipPort);
			if (done(connect) && no(socket)) {
				socket = new SocketBay(connect.result());
				socket.upload().add(hello);
			}

			// Download and check the peer's response
			if (is(socket)) {
				try {
					Outline o = new Outline(socket.download().data().clip());
					if (o.toData().hash().data.start(6).equals(hash))
						close(Connect.this);
					else
						throw new DataException("bad response");
				} catch (ChopException e) { Mistake.ignore(e); }
			}

		} catch (ProgramException e) { exception = e; close(this); }
	}
}
