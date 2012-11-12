package org.zootella.base.user;

import org.junit.Test;
import org.junit.Assert;

public class DescribeTest {
	
	@Test public void testDecimal() throws Exception {

		//3
		
		//small
		Assert.assertEquals(Describe.decimal(0, 3),      "0.000");
		Assert.assertEquals(Describe.decimal(1, 3),      "0.001");
		Assert.assertEquals(Describe.decimal(12, 3),     "0.012");
		Assert.assertEquals(Describe.decimal(123, 3),    "0.123");
		Assert.assertEquals(Describe.decimal(1234, 3),   "1.234");
		Assert.assertEquals(Describe.decimal(12345, 3), "12.345");

		//big
		Assert.assertEquals(Describe.decimal(1234567, 3), "1,234.567");
		Assert.assertEquals(Describe.decimal(123456789, 3), "123,456.789");

		//negative
		Assert.assertEquals(Describe.decimal(-1, 3), "-0.001");
		Assert.assertEquals(Describe.decimal(-123456789, 3), "-123,456.789");

		//2
		
		//small
		Assert.assertEquals(Describe.decimal(0, 2),      "0.00");
		Assert.assertEquals(Describe.decimal(1, 2),      "0.01");
		Assert.assertEquals(Describe.decimal(12, 2),     "0.12");
		Assert.assertEquals(Describe.decimal(123, 2),    "1.23");
		Assert.assertEquals(Describe.decimal(1234, 2),   "12.34");
		Assert.assertEquals(Describe.decimal(12345, 2), "123.45");

		//big
		Assert.assertEquals(Describe.decimal(1234567, 2), "12,345.67");
		Assert.assertEquals(Describe.decimal(123456789, 2), "1,234,567.89");

		//negative
		Assert.assertEquals(Describe.decimal(-1, 2), "-0.01");
		Assert.assertEquals(Describe.decimal(-123456789, 2), "-1,234,567.89");

		//1
		
		//small
		Assert.assertEquals(Describe.decimal(0, 1),      "0.0");
		Assert.assertEquals(Describe.decimal(1, 1),      "0.1");
		Assert.assertEquals(Describe.decimal(12, 1),     "1.2");
		Assert.assertEquals(Describe.decimal(123, 1),    "12.3");
		Assert.assertEquals(Describe.decimal(1234, 1),   "123.4");
		Assert.assertEquals(Describe.decimal(12345, 1), "1,234.5");

		//big
		Assert.assertEquals(Describe.decimal(1234567, 1), "123,456.7");
		Assert.assertEquals(Describe.decimal(123456789, 1), "12,345,678.9");

		//negative
		Assert.assertEquals(Describe.decimal(-1, 1), "-0.1");
		Assert.assertEquals(Describe.decimal(-123456789, 1), "-12,345,678.9");
	}
}
