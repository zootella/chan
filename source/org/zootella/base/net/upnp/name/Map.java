package org.zootella.base.net.upnp.name;

import org.zootella.base.net.name.IpPort;
import org.zootella.base.net.name.Port;

public class Map {

	public Map(Port outside, IpPort inside, String protocol, String description) {
		this.outsidePort = outside;
		this.inside      = inside;
		this.protocol    = protocol;
		this.description = description;
	}

	public final Port outsidePort;
	public final IpPort inside;
	public final String protocol;
	public final String description;
}
