package org.zootella.base.encrypt.store;

import org.junit.Assert;
import org.junit.Test;
import org.zootella.base.data.Data;
import org.zootella.base.encrypt.store.Password;

public class PasswordTest {
	
	@Test public void test() throws Exception {

		String message = "Here is an example secret.";
		String password = "12345";
		
		Data data = new Data(message);
		
		Data encrypted = Password.scramble(data, password);
		Data decrypted = Password.unscramble(encrypted, password);
		
		Assert.assertEquals(data, decrypted);
	}
}
