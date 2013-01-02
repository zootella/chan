package org.zootella.base.encrypt.small;

import org.zootella.base.data.Data;
import org.zootella.base.encrypt.hash.Hash;

public class HashValue {
	
	public HashValue(Data data) {
		if (data.size() != Hash.size) throw new IllegalArgumentException();
		this.data = data;
	}
	
	public final Data data;

}
