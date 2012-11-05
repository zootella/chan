package org.zootella.demo.here;

import org.zootella.base.data.Outline;
import org.zootella.base.net.name.Ip;
import org.zootella.base.net.name.IpPort;
import org.zootella.base.net.name.Port;
import org.zootella.base.net.packet.Packets;
import org.zootella.base.net.upnp.Router;
import org.zootella.base.net.upnp.name.Map;
import org.zootella.base.state.Close;
import org.zootella.base.state.Result;
import org.zootella.base.user.Describe;

public class HereCore extends Close {

	public HereCore(Packets packets, Port port) {
		this.packets = packets;
		this.port = port;
		
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
//			model.changed(); Don't need to replace this with soon() because pulseUser() will get called after all the pulse() already
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
		soon();
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
	
	// Model
	
	//methods that compose text for the user
	//any view you want can call them whenever it wants
	//views will probably call them in a pulseUser() method, though
	//and check to only update the screen if what is returned is different from what is on the screen, to avoid blinking

	public String userIps() {
		if (HereCore.this.net() == null || HereCore.this.lan() == null) return "";
		return HereCore.this.net().toString() + " -> " + HereCore.this.lan().toString();
	}
	
	public String userLanIp()    { return Describe.describe(HereCore.this.lanIp()); }
	public String userBindPort() { return Describe.describe(HereCore.this.bindPort()); }
	public String userNatModel() { return Describe.describe(HereCore.this.natModel()); }
	public String userNatIp()    { return Describe.describe(HereCore.this.natIp()); }
	public String userMapTcp()   { return Describe.describe(HereCore.this.mapTcp()); }
	public String userMapUdp()   { return Describe.describe(HereCore.this.mapUdp()); }
	public String userCenterIp() { return Describe.describe(HereCore.this.centerIpPort()); }
	
	public String userLanIpTime()    { return Describe.describeTime(HereCore.this.lanIp()); }
	public String userBindPortTime() { return Describe.describeTime(HereCore.this.bindPort()); }
	public String userNatModelTime() { return Describe.describeTime(HereCore.this.natModel()); }
	public String userNatIpTime()    { return Describe.describeTime(HereCore.this.natIp()); }
	public String userMapTcpTime()   { return Describe.describeTime(HereCore.this.mapTcp()); }
	public String userMapUdpTime()   { return Describe.describeTime(HereCore.this.mapUdp()); }
	public String userCenterIpTime() { return Describe.describeTime(HereCore.this.centerIpPort()); }
	
	public String userLanIpError()    { return Describe.describeError(HereCore.this.lanIp()); }
	public String userBindPortError() { return Describe.describeError(HereCore.this.bindPort()); }
	public String userNatModelError() { return Describe.describeError(HereCore.this.natModel()); }
	public String userNatIpError()    { return Describe.describeError(HereCore.this.natIp()); }
	public String userMapTcpError()   { return Describe.describeError(HereCore.this.mapTcp()); }
	public String userMapUdpError()   { return Describe.describeError(HereCore.this.mapUdp()); }
	public String userCenterIpError() { return Describe.describeError(HereCore.this.centerIpPort()); }
}
