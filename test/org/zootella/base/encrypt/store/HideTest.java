package org.zootella.base.encrypt.store;

import org.junit.Assert;
import org.junit.Test;

public class HideTest {

	@Test public void test() throws Exception {

		Assert.assertEquals(-128, (int)Byte.MIN_VALUE);
		Assert.assertEquals(127, (int)Byte.MAX_VALUE);
		
		Assert.assertEquals(256, -1*(int)Byte.MIN_VALUE + 1 + (int)Byte.MAX_VALUE);
		Assert.assertEquals(128, -1*(int)Byte.MIN_VALUE);
	}
}
