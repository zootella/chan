package org.zootella.main;

import org.zootella.base.state.Close;

public class NullUnit extends Close {

	@Override public void close() {
		if (already()) return;
		
	}

}
