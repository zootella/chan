package org.zootella.demo.here;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.zootella.base.exception.NetException;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.net.name.Ip;
import org.zootella.base.state.Result;
import org.zootella.base.time.Duration;
import org.zootella.base.time.Now;

public class LanIp {
	
	public static Result<Ip> ip() {
		Ip ip = null;
		Now now = new Now();
		ProgramException exception = null;
		
		try {
			ip = new Ip(InetAddress.getLocalHost());
		} catch (UnknownHostException e) { exception = new NetException(e); }
		Duration duration = new Duration(now);
		
		return new Result<Ip>(ip, duration, exception);
	}
}
