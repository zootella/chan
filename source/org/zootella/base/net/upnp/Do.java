package org.zootella.base.net.upnp;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.zootella.base.exception.NetException;
import org.zootella.base.net.name.Ip;
import org.zootella.base.net.upnp.name.Map;
import org.zootella.base.state.Result;
import org.zootella.base.time.Now;

public class Do {

	public static ControlPoint start(DeviceChangeListener listen) {
		ControlPoint c = new ControlPoint();
		c.addDeviceChangeListener(listen);
		c.start();
		return c;
	}

	public static Result<Ip> ip(Access device) {
		Now start = new Now();
			
		Action a = device.action("GetExternalIPAddress");
		if (a == null) throw new NetException("null action");
		if (!a.postControlAction()) throw new NetException("post false");
		
		Argument r = a.getOutputArgumentList().getArgument("NewExternalIPAddress");
		Ip ip = new Ip(r.getValue());
		return new Result<Ip>(ip, start);
	}

	public static Result<Map> add(Access access, Map map) {
		Now start = new Now();
		
		Action a = access.action("AddPortMapping");
		if (a == null) throw new NetException("null action");

		a.setArgumentValue("NewRemoteHost",             "");                       // String
		a.setArgumentValue("NewExternalPort",           map.outsidePort.port);     // int
		a.setArgumentValue("NewInternalClient",         map.inside.ip.toString()); // String
		a.setArgumentValue("NewInternalPort",           map.inside.port.port);     // int
		a.setArgumentValue("NewProtocol",               map.protocol);             // String
		a.setArgumentValue("NewPortMappingDescription", map.description);          // String
		a.setArgumentValue("NewEnabled",                "1");                      // String
		a.setArgumentValue("NewLeaseDuration",          0);                        // int
		
		boolean b = a.postControlAction();
		if (!b) throw new NetException("post false");
		return new Result<Map>(map, start);
	}
	
	public static Result<Map> remove(Access access, Map map) {
		Now start = new Now();
		
		Action a = access.action("DeletePortMapping");
		if (a == null) throw new NetException("null action");

		a.setArgumentValue("NewRemoteHost",   "");                   // String
		a.setArgumentValue("NewExternalPort", map.outsidePort.port); // int
		a.setArgumentValue("NewProtocol",     map.protocol);         // String

		boolean b = a.postControlAction();
		if (!b) throw new NetException("post false");
		return new Result<Map>(map, start);
	}
}
