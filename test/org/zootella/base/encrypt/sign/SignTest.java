package org.zootella.base.encrypt.sign;

import org.junit.Assert;
import org.junit.Test;
import org.zootella.base.data.Data;
import org.zootella.base.encrypt.sign.Sign;
import org.zootella.base.encrypt.sign.SignKey;

public class SignTest {
	
	@Test public void test() throws Exception {

		SignKey key = Sign.make();
		Data signature = Sign.sign(new Data("hello"), key);
		Assert.assertTrue(Sign.verify(new Data("hello"), signature, key));
		Assert.assertFalse(Sign.verify(new Data("hEllo"), signature, key));
	}
}
