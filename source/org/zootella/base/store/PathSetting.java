package org.zootella.base.store;

import org.zootella.base.data.Outline;
import org.zootella.base.exception.DataException;
import org.zootella.base.file.Path;

public class PathSetting {
	
	public PathSetting(Outline outline, String name, Path program) {
		setting = new StringSetting(outline, name, program.toString());
		this.program = program;
	}
	private final StringSetting setting;
	private final Path program;
	
	public void set(Path value) { setting.set(value.toString()); }
	public Path value() {
		try {
			return new Path(setting.value());
		} catch (DataException e) { return program; }
	}
}
