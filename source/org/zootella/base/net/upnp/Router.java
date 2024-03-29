package org.zootella.base.net.upnp;

import org.cybergarage.upnp.ControlPoint;
import org.zootella.base.data.Outline;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.net.name.Ip;
import org.zootella.base.net.upnp.name.Map;
import org.zootella.base.net.upnp.task.AddTask;
import org.zootella.base.net.upnp.task.IpTask;
import org.zootella.base.net.upnp.task.StartTask;
import org.zootella.base.process.Mistake;
import org.zootella.base.state.Close;
import org.zootella.base.state.Result;
import org.zootella.base.time.Egg;
import org.zootella.base.time.Now;
import org.zootella.base.time.Time;

public class Router extends Close {
	
	public Router(Map tcp, Map udp) {
		whenMade = new Now();
		
		egg = new Egg(20 * Time.second);

		tcpMap = tcp;
		udpMap = udp;

		listen = new Listen();
		startTask = new StartTask(listen.listen);
		log("start");
	}
	
	private final Now whenMade;
	
	private final Map tcpMap;
	private final Map udpMap;
	
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
		
		close(udpTask);
		close(tcpTask);
		close(ipTask);
		
		try {
			if (control != null)
				control.stop();
		} catch (Throwable t) { Mistake.log(t); }
		
		close(startTask);
	}

	@Override public void pulse() {
		try {
			
			egg.check();
			
			if (control == null && done(startTask))
				control = startTask.result();
			if (access == null && listen.access() != null) {
				access = listen.access();
				nameResult = new Result<Outline>(access.o, whenMade);
				soon();
				log("access " + access.o.value("friendlyname"));
			}
			
			if (no(ipTask) && access != null)
				ipTask = new IpTask(listen.access());
			if (ipResult == null && done(ipTask)) {
				ipResult = ipTask.result();
				soon();
				log("ip " + ipResult.result().toString() + " " + ipResult.duration.toString());
			}
			
			if (no(tcpTask) && access != null)
				tcpTask = new AddTask(access, tcpMap);
			if (tcpResult == null && done(tcpTask)) {
				tcpResult = tcpTask.result();
				soon();
				log("tcp " + " " + tcpResult.duration.toString());
			}
			
			if (no(udpTask) && access != null)
				udpTask = new AddTask(access, udpMap);
			if (udpResult == null && done(udpTask)) {
				udpResult = udpTask.result();
				soon();
				log("udp " + " " + udpResult.duration.toString());
			}

		} catch (ProgramException e) { exception(e); close(this); return; }
	}
	
	private void exception(ProgramException e) {
		if (nameResult == null)
			nameResult = new Result<Outline>(null, whenMade, e);
		if (ipResult == null)
			ipResult = new Result<Ip>(null, whenMade, e);
		if (tcpResult == null)
			tcpResult = new Result<Map>(null, whenMade, e);
		if (udpResult == null)
			udpResult = new Result<Map>(null, whenMade, e);
	}
}
