package org.zootella.base.encode;

import org.junit.Assert;

import org.junit.Test;
import org.zootella.base.data.Data;
import org.zootella.base.data.Encode;
import org.zootella.base.data.Quote;
import org.zootella.base.exception.DataException;
import org.zootella.base.state.OldClose;

public class EncodeTest {
	
	@Test public void test() throws Exception {
		
		test("");
		test("a");
		test("ab");
		test("[");
		test("]");
		test("\0");
		test("\t");
		test("a[b]c\r\n");
		test("[hello]\0\t\r\n");

		test(Data.random(16));
	}

	public void test(String a) throws Exception {
			
		String b = Encode.box(new Data(a));
		String c = Encode.unbox(b).toString();
		Assert.assertTrue(a.equals(c));
	}

	public void test(Data a) throws Exception {
			
		String b = Encode.box(a);
		Data c = Encode.unbox(b);
		Assert.assertTrue(a.equals(c));
	}
	
	
	
	@Test public void testQuote() throws Exception {

		
		
		Assert.assertTrue(Quote.text(stringToByte("a")) == true);
		Assert.assertTrue(Quote.text(stringToByte("5")) == true);
		Assert.assertTrue(Quote.text(stringToByte(" ")) == true);
		Assert.assertTrue(Quote.text(stringToByte("~")) == true);
		Assert.assertTrue(Quote.text(stringToByte("'")) == true);
		
		Assert.assertTrue(Quote.text(stringToByte("\"")) == false);
		Assert.assertTrue(Quote.text(stringToByte("\0")) == false);
		Assert.assertTrue(Quote.text(stringToByte("\r")) == false);
		Assert.assertTrue(Quote.text(stringToByte("\n")) == false);
		
		OldClose.log("hello");
		
		
		

		testQuote("", "");
		testQuote("a", "\"a\"");
		testQuote(" ", "\" \"");
		testQuote("\0", "00");
		testQuote("Hello", "\"Hello\"");
		testQuote("Hello You\r\n", "\"Hello You\"0d0a");
		testQuote("He only says \"Yes\" once a year.\r\n", "\"He only says \"22\"Yes\"22\" once a year.\"0d0a");
		
		testQuote(  "a\tb\tc",     "\"a\"09\"b\"09\"c\"");
		testQuote("\ta\tb\tc",   "09\"a\"09\"b\"09\"c\"");
		testQuote(  "a\tb\tc\t",   "\"a\"09\"b\"09\"c\"09");
		testQuote("\ta\tb\tc\t", "09\"a\"09\"b\"09\"c\"09");
		
		testQuote("The quote \" character\r\n", "\"The quote \"22\" character\"0d0a");		
		
		
	}
	
	
	private void testQuote(String before, String after) {
		
		String s = Encode.quote(new Data(before));
		Data d = Encode.unquote(s);
		
		Assert.assertEquals(before, d.toString()); // Make sure quoting and dequoting gets us back to the original data
		Assert.assertEquals(after, s); // Confirm we predicted what the quoted text would look like

		OldClose.log("");
		OldClose.log(before);
		OldClose.log(after);
	}
	
	private void testUnquote(String before, String after, boolean valid) {

		if (valid) {
			
			Data d = Encode.unquote(before);
			String s = Encode.quote(d);
			
			Assert.assertEquals(before, s);//confirm the round trip didn't change it
			Assert.assertEquals(after, d.toString());//confirm it became what was predicted
			
		} else {
			
			try {
				Data d = Encode.unquote(before);
				Assert.fail();
			} catch (DataException e) {}
		}
		
	}

	
	
	
	byte stringToByte(String s) {
		Data d = new Data(s);
		return d.first();
	}
	
	
	
	
}
