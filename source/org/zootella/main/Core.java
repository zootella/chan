package org.zootella.main;

import org.zootella.base.data.Data;
import org.zootella.base.exception.DataException;
import org.zootella.base.net.accept.Accept;
import org.zootella.base.net.name.Port;
import org.zootella.base.net.packet.Packets;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.demo.here.Here;

/** The core program beneath the window that does everything. */
public class Core extends Close {

	public Core(Program program) {
		this.program = program;
		
		Port p = null;
		try {
			p = new Port(program.store.o.o("here").value("port").toString());
		} catch (DataException e) { Mistake.ignore(e); }
		if (p == null) {
			p = Port.random();
			program.store.o.m("here").add("port", new Data(p.toString()));
		}
		port = p;

		accept = new Accept(port);
		packets = new Packets(port);
		here = new Here(packets, port);
	}

	private final Program program;
	public final Accept accept;
	public final Packets packets;

	private final Port port;
	public final Here here;
	
	@Override public void close() {
		if (already()) return;
		
		close(accept);
		close(packets);
		close(here);
	}
}
