package org.zootella.base.net.upnp;

import org.cybergarage.upnp.ControlPoint;
import org.zootella.base.data.Outline;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.exception.TimeException;
import org.zootella.base.net.name.Ip;
import org.zootella.base.net.upnp.name.Map;
import org.zootella.base.net.upnp.task.AddTask;
import org.zootella.base.net.upnp.task.IpTask;
import org.zootella.base.net.upnp.task.StartTask;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.base.state.Receive;
import org.zootella.base.state.Result;
import org.zootella.base.state.Update;
import org.zootella.base.time.Egg;
import org.zootella.base.time.Time;

public class Router extends Close {
	
	public Router(Update up, Map tcp, Map udp) {
		this.up = up;
		
		Receive receive = new MyReceive();
		update = new Update(receive);
		egg = new Egg(receive, 20 * Time.second);

		tcpMap = tcp;
		udpMap = udp;

		listen = new Listen(update);
		startTask = new StartTask(update, listen.listen);
		log("start");
	}
	
	private final Map tcpMap;
	private final Map udpMap;
	
	private final Update up;
	private final Update update;
	private final Egg egg;
	
	private Listen listen;
	private StartTask startTask;
	private ControlPoint control;
	private Access access;
	
	private Result<Outline> nameResult;
	public Result<Outline> name() { return nameResult; }
	public boolean hasName() { return nameResult != null; }
	
	private IpTask ipTask;
	private Result<Ip> ipResult;
	public Result<Ip> ip() { return ipResult; }
	public boolean hasIp() { return ipResult != null; }
	
	private AddTask tcpTask;
	private Result<Map> tcpResult;
	public Result<Map> tcp() { return tcpResult; }
	public boolean hasTcp() { return tcpResult != null; }
	
	private AddTask udpTask;
	private Result<Map> udpResult;
	public Result<Map> udp() { return udpResult; }
	public boolean hasUdp() { return udpResult != null; }

	@Override public void close() {
		if (already()) return;
		
		close(egg);
		close(udpTask);
		close(tcpTask);
		close(ipTask);
		
		try {
			if (control != null)
				control.stop();
		} catch (Throwable t) { Mistake.log(t); }
		
		close(startTask);
		
		up.send();
	}

	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {
				
				egg.check();
				
				if (control == null && done(startTask))
					control = startTask.result();
				if (access == null && listen.access() != null) {
					access = listen.access();
					nameResult = new Result<Outline>(access.o, whenMade());
					up.send();
					log("access " + access.o.value("friendlyname"));
				}
				
				if (no(ipTask) && access != null)
					ipTask = new IpTask(update, listen.access());
				if (ipResult == null && done(ipTask)) {
					ipResult = ipTask.result();
					up.send();
					log("ip " + ipResult.result().toString() + " " + ipResult.duration.toString());
				}
				
				if (no(tcpTask) && access != null)
					tcpTask = new AddTask(update, access, tcpMap);
				if (tcpResult == null && done(tcpTask)) {
					tcpResult = tcpTask.result();
					up.send();
					log("tcp " + " " + tcpResult.duration.toString());
				}
				
				if (no(udpTask) && access != null)
					udpTask = new AddTask(update, access, udpMap);
				if (udpResult == null && done(udpTask)) {
					udpResult = udpTask.result();
					up.send();
					log("udp " + " " + udpResult.duration.toString());
				}

			} catch (ProgramException e) { exception(e); close(Router.this); return; }
		}
	}
	
	private void exception(ProgramException e) {
		if (nameResult == null)
			nameResult = new Result<Outline>(null, whenMade(), e);
		if (ipResult == null)
			ipResult = new Result<Ip>(null, whenMade(), e);
		if (tcpResult == null)
			tcpResult = new Result<Map>(null, whenMade(), e);
		if (udpResult == null)
			udpResult = new Result<Map>(null, whenMade(), e);
	}
}
