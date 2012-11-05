package org.zootella.demo.here;

import org.zootella.base.data.Outline;
import org.zootella.base.net.name.Ip;
import org.zootella.base.net.name.IpPort;
import org.zootella.base.net.name.Port;
import org.zootella.base.net.packet.Packets;
import org.zootella.base.net.upnp.Router;
import org.zootella.base.net.upnp.name.Map;
import org.zootella.base.state.Close;
import org.zootella.base.state.Model;
import org.zootella.base.state.Result;

public class HereCore extends Close {

	public HereCore(Packets packets, Port port) {
		this.packets = packets;
		this.port = port;
		model = new MyModel();
		
		refreshLan();
		refreshBind();
		refreshNat();
		refreshCenter();
	}
	
	private final Packets packets;
	private final Port port;
	
	private CenterTask centerTask;

	@Override public void close() {
		if (already()) return;
		close(model);
		close(centerTask);
		close(router);
	}
	

	@Override public void pulse() {
		
		if (is(router)) {
			if (natModel == null && router.hasName())
				natModel = router.name();
			if (natIp == null && router.hasIp())
				natIp = router.ip();
			if (mapTcp == null && router.hasTcp())
				mapTcp = router.tcp();
			if (mapUdp == null && router.hasUdp())
				mapUdp = router.udp();
		}

		if (done(centerTask)) {
			centerIpPort = centerTask.result();
			centerTask = null;
			model.changed();
		}
	}
	
	
	
	
	// summary
	public IpPort net() { return null; }
	public IpPort lan() { return null; }

	// value, time, error
	public Result<Ip>      lanIp()    { return lanIp; }
	public Result<Port>    bindPort() { return bindPort; }
	public Result<Outline> natModel() { return natModel; }
	public Result<Ip>      natIp()    { return natIp; }
	public Result<Map>     mapTcp()   { return mapTcp; }
	public Result<Map>     mapUdp()   { return mapUdp; }
	public Result<IpPort>  centerIpPort() { return centerIpPort; }
	
	private Result<Ip> lanIp;
	private Result<Port> bindPort;
	private Result<Outline> natModel;
	private Result<Ip> natIp;
	private Result<Map> mapTcp;
	private Result<Map> mapUdp;
	private Result<IpPort> centerIpPort;
	
	// refresh
	public void refreshLan() {
		lanIp = LanIp.ip();
		model.changed();
	}
	public void refreshBind() {}
	public void refreshNat() {
		close(router);
		
		natModel = null;
		natIp = null;
		mapTcp = null;
		mapUdp = null;
		
		IpPort l = new IpPort(LanIp.ip().result(), port);
		Map t = new Map(port, l, "TCP", "Pipe");
		Map u = new Map(port, l, "UDP", "Pipe");
		router = new Router(t, u);
		soon();
	}
	public void refreshCenter() {
		close(centerTask);
		centerTask = new CenterTask(packets);
		soon();
	}
	
	private Router router;

	public final MyModel model;
	public class MyModel extends Model {
		
		public String ips() {
			if (HereCore.this.net() == null || HereCore.this.lan() == null) return "";
			return HereCore.this.net().toString() + " -> " + HereCore.this.lan().toString();
		}
		
		public String lanIp()    { return describe(HereCore.this.lanIp()); }
		public String bindPort() { return describe(HereCore.this.bindPort()); }
		public String natModel() { return describe(HereCore.this.natModel()); }
		public String natIp()    { return describe(HereCore.this.natIp()); }
		public String mapTcp()   { return describe(HereCore.this.mapTcp()); }
		public String mapUdp()   { return describe(HereCore.this.mapUdp()); }
		public String centerIp() { return describe(HereCore.this.centerIpPort()); }

		public String lanIpTime()    { return describeTime(HereCore.this.lanIp()); }
		public String bindPortTime() { return describeTime(HereCore.this.bindPort()); }
		public String natModelTime() { return describeTime(HereCore.this.natModel()); }
		public String natIpTime()    { return describeTime(HereCore.this.natIp()); }
		public String mapTcpTime()   { return describeTime(HereCore.this.mapTcp()); }
		public String mapUdpTime()   { return describeTime(HereCore.this.mapUdp()); }
		public String centerIpTime() { return describeTime(HereCore.this.centerIpPort()); }

		public String lanIpError()    { return describeError(HereCore.this.lanIp()); }
		public String bindPortError() { return describeError(HereCore.this.bindPort()); }
		public String natModelError() { return describeError(HereCore.this.natModel()); }
		public String natIpError()    { return describeError(HereCore.this.natIp()); }
		public String mapTcpError()   { return describeError(HereCore.this.mapTcp()); }
		public String mapUdpError()   { return describeError(HereCore.this.mapUdp()); }
		public String centerIpError() { return describeError(HereCore.this.centerIpPort()); }
	}
}
