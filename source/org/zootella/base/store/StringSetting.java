package org.zootella.base.store;

import org.zootella.base.data.Data;
import org.zootella.base.data.Outline;

public class StringSetting {
	
	public StringSetting(Outline outline, String name, String program) {
		setting = new DataSetting(outline, name, new Data(program));
	}
	private final DataSetting setting;
	
	public void set(String value) { setting.set(new Data(value)); }
	public String value() { return setting.value().toString(); }
}
