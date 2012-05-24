package org.zootella.base.store;

import org.zootella.base.data.Outline;
import org.zootella.base.exception.DataException;
import org.zootella.base.net.name.IpPort;

public class IpPortSetting {

	public IpPortSetting(Outline outline, String name, IpPort program) {
		setting = new StringSetting(outline, name, program.toString());
		this.program = program;
	}
	private final StringSetting setting;
	private final IpPort program;
	
	public void set(IpPort value) { setting.set(value.toString()); }
	public IpPort value() {
		try {
			return new IpPort(setting.value());
		} catch (DataException e) { return program; }
	}
}
