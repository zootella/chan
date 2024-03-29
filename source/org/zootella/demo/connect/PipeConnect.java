package org.zootella.demo.connect;

import org.zootella.base.data.Data;
import org.zootella.base.data.Outline;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.net.accept.AcceptReceive;
import org.zootella.base.net.connect.Connect;
import org.zootella.base.net.flow.SocketBay;
import org.zootella.base.net.name.IpPort;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.base.time.Ago;
import org.zootella.main.Program;

public class PipeConnect extends Close {
	
	/** Keep trying to upload hello to the peer at lan and internet, or let it connect to us, until it says hash back. */
	public PipeConnect(Program program, IpPort lan, IpPort internet, Data hello, Data hash) {
		this.program = program;
		this.lanIp = lan;
		this.netIp = internet;
		this.hello = hello;
		this.hash = hash;
		
		acceptReceive = new MyAcceptReceive();
		program.core.accept.add(acceptReceive);
		
		lanAgo = new Ago();
		netAgo = new Ago();
	}
	
	private final Program program;
	
	private final IpPort lanIp; // remote peer's lan ip address
	private final IpPort netIp; // remote peer's internet ip address
	private final Data hello; // our greeting we'll send the remote peer
	private final Data hash; // the first outline the remote peer send us must hash to this value
	
	private Connect lan;
	private Connect net;
	
	private final Ago lanAgo;
	private final Ago netAgo;

	@Override public void close() {
		if (already()) return;
		close(lan);
		close(net);
		try { program.core.accept.remove(acceptReceive); } catch (Throwable t) { Mistake.log(t); }
	}
	
	/** When done() call result() to get the SocketBay with the peer's valid greeting sitting in download(). */
	public SocketBay result() { return socket; }
	private SocketBay socket; // as soon as you get socket, you close

	@Override public void pulse() {

		// Connect to peer's LAN address
		if (no(lan) && lanAgo.enough()) {
			lan = new Connect(lanIp, hello, hash);
			attempts++;
		}
		if (done(lan)) {
			try {
				socket = lan.result(); // As soon as we have socket, close and return
				close(PipeConnect.this);
				return;
			} catch (ProgramException e) { Mistake.ignore(e); }
			lan = null;
		}

		// Connect to peer's Internet address
		if (no(net) && netAgo.enough())
			net = new Connect(netIp, hello, hash);
		if (done(net)) {
			try {
				socket = net.result();
				close(PipeConnect.this);
				return;
			} catch (ProgramException e) { Mistake.ignore(e); }
			net = null;
		}
	}
	
	private final MyAcceptReceive acceptReceive;
	private class MyAcceptReceive implements AcceptReceive {
		public boolean receive(SocketBay s) {
			if (closed()) return false;
			try {
				
				// See if the peer has connected to us
				if ((new Outline(s.download().data().clip())).toData().hash().data.start(6).same(hash)) {
					socket = s;
					socket.upload().add(hello);
					close(PipeConnect.this);
					return true;
				}
				
			} catch (ProgramException e) { Mistake.ignore(e); }
			return false;
		}
	}
	
	//tell the user about us
	
	private int attempts;
	public int attempts() { return attempts; }
	
	
	
	
}
