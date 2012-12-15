package org.zootella.base.encrypt.password;

import org.junit.Assert;
import org.junit.Test;
import org.zootella.base.data.Data;

public class PasswordTest {
	
	@Test public void test() throws Exception {

		String message = "Here is an example secret.";
		String password = "12345";
		
		Data data = new Data(message);
		
		Data encrypted = Password.encrypt(data, password);
		Data decrypted = Password.decrypt(encrypted, password);
		
		Assert.assertEquals(data, decrypted);
	}
}
