package org.zootella.base.net.accept;

import java.util.HashSet;
import java.util.Set;

import org.zootella.base.list.TwoBoots;
import org.zootella.base.net.flow.SocketBay;
import org.zootella.base.net.name.Port;
import org.zootella.base.state.Close;
import org.zootella.base.state.Update;
import org.zootella.base.time.Time;

/** The program's Accept object listens on a port to accept new incoming TCP socket connections. */
public class Accept extends Close {
	
	public Accept(Port port) {
		
		listenSocket = new ListenSocket(port);
		sockets = new TwoBoots<SocketBay>(Time.out);
		receivers = new HashSet<AcceptReceive>();

		update = new Update();
		update.send();
	}
	
	private final Update update;
	
	private final ListenSocket listenSocket;
	private AcceptTask acceptTask;
	
	private final TwoBoots<SocketBay> sockets;
	private final Set<AcceptReceive> receivers;

	@Override public void close() {
		if (already()) return;
		close(listenSocket);
		close(acceptTask);
		close(sockets);
	}

	@Override public void pulse() {
		
		// Wait for new sockets to connect
		if (done(acceptTask)) {
			sockets.add(new SocketBay(update, acceptTask.result()));
			acceptTask = null;
		}
		if (no(acceptTask))
			acceptTask = new AcceptTask(update, listenSocket);

		// Show each AcceptReceive object above each socket that has connected in
		for (AcceptReceive r : new HashSet<AcceptReceive>(receivers))
			for (SocketBay s : sockets.list())
				if (r.receive(s))
					sockets.remove(s); // r took s, remove s from our list
	}
	
	/** Add o to the list of objects this Packets object shows the packets it receives. */
	public void add(AcceptReceive o) {
		confirmOpen(); // If this object is closed, we can't let it change, throw an exception
		if (!receivers.contains(o))
			receivers.add(o);
		update.send();
	}
	
	/** Remove o from the list of objects this Packets object bothers with arrived packets. */
	public void remove(AcceptReceive o) {
		if (closed()) return; // If this object is closed, we can't let it change, do nothing
		receivers.remove(o);
	}
}
