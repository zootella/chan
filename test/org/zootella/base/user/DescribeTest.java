package org.zootella.base.user;

import org.junit.Test;
import org.junit.Assert;

public class DescribeTest {
	
	@Test public void testThousandths() throws Exception {
		
		//small
		Assert.assertEquals(Describe.thosandths(0),      "0.000");
		Assert.assertEquals(Describe.thosandths(1),      "0.001");
		Assert.assertEquals(Describe.thosandths(12),     "0.012");
		Assert.assertEquals(Describe.thosandths(123),    "0.123");
		Assert.assertEquals(Describe.thosandths(1234),   "1.234");
		Assert.assertEquals(Describe.thosandths(12345), "12.345");

		//big
		Assert.assertEquals(Describe.thosandths(1234567), "1,234.567");
		Assert.assertEquals(Describe.thosandths(123456789), "123,456.789");

		//negative
		Assert.assertEquals(Describe.thosandths(-1), "-0.001");
		Assert.assertEquals(Describe.thosandths(-123456789), "-123,456.789");
	}
}
