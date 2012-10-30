package org.zootella.base.encode;

import org.junit.Assert;
import org.junit.Test;
import org.zootella.base.data.Data;
import org.zootella.base.data.Encode;
import org.zootella.base.data.Text;
import org.zootella.base.exception.DataException;
import org.zootella.base.process.Log;

public class EncodeTest {
	
	@Test public void test() throws Exception {
		
		//blank
		testQuote("", "");
		
		//single text and data
		testQuote("a", "|a|"); // Code below uses Text.quote to turn | into " to make the string literals more readable
		testQuote("\r", "0d");

		//special character
		testQuote("|", "22");
		
		//longer samples
		testQuote("a and here is some text|b|c\r\n", "|a and here is some text|22|b|22|c|0d0a");
		testQuote("hello\0\t\r\n", "|hello|00090d0a");
		
		//true and false
		testQuote("t", "|t|");
		testQuote("f", "|f|");

		//tab
		testQuote("\ttab\t", "09|tab|09");

		//invalid
		testUnquoteInvalid("poop");
		testUnquoteInvalid("|value");
		testUnquoteInvalid("0a0b|value");
		testUnquoteInvalid("|hello you|0d0a poop#comment");
		testUnquoteInvalid("|hello you|0d0apoop#comment");
		testUnquoteInvalid("poop|hello you|0d0a");
		testUnquoteInvalid("poop|hello you|");
		
		//comment
		testUnquote("|value|0d0a #comment", "value\r\n");
		testUnquote("|hello you|0d0a",          "hello you\r\n");
		testUnquote("|hello you|0d0a#comment",  "hello you\r\n");
		testUnquote("|hello you|0d0a #comment", "hello you\r\n");
		testUnquote("|room #9|0d0a",          "room #9\r\n");
		testUnquote("|room #9|0d0a#comment",  "room #9\r\n");
		testUnquote("|room #9|0d0a #comment", "room #9\r\n");
		
		//a comment can say whatever
		testUnquote("|hello|0d0a #note, with |quotes| and stuff", "hello\r\n");
		
		//comments different places
		testUnquote("0d0a #note", "\r\n");
		testUnquote("0d0a#note", "\r\n");
		testUnquote("#note", "");
		testUnquote(" #note", ""); //TODO works but maybe shouldn't
		testUnquote(" 0d0a #note", "\r\n"); //this one too, leading space is wrong but allowed

		//another batch
		testQuote("", "");
		testQuote("a", "|a|");
		testQuote(" ", "| |");
		testQuote("\0", "00");
		testQuote("Hello", "|Hello|");
		testQuote("Hello You\r\n", "|Hello You|0d0a");
		testQuote("He only says |Yes| once a year.\r\n", "|He only says |22|Yes|22| once a year.|0d0a");
		
		//lots of tabs
		testQuote(  "aaa\tbbb\tccc",     "|aaa|09|bbb|09|ccc|");
		testQuote("\taaa\tbbb\tccc",   "09|aaa|09|bbb|09|ccc|");
		testQuote(  "aaa\tbbb\tccc\t",   "|aaa|09|bbb|09|ccc|09");
		testQuote("\taaa\tbbb\tccc\t", "09|aaa|09|bbb|09|ccc|09");
		
		//the quote character
		testQuote("The quote | character\r\n", "|The quote |22| character|0d0a");

		//pound is ok in a quote
		testUnquote("|car #1|0d0a #comment", "car #1\r\n");

		//random data
		for (int i = 1; i < 20; i++)
			testQuoteData(Data.random(20));
	}
	
	private void testQuoteData(Data before) {
		
		String s = Encode.quote(before);
		Data d = Encode.unquote(s);
		
		Log.log(s);
		
		Assert.assertEquals(before, d); // Make sure quoting and dequoting gets us back to the original data
	}
	
	private void testQuote(String before, String after) {
		before = Text.quote(before);
		after = Text.quote(after);
		
		String s = Encode.quote(new Data(before));
		Data d = Encode.unquote(s);

		Log.log("");
		Log.log(before);
		Log.log(after);
		
		Assert.assertEquals(before, d.toString()); // Make sure quoting and dequoting gets us back to the original data
		Assert.assertEquals(after, s); // Confirm we predicted what the quoted text would look like
	}
	
	private void testUnquote(String before, String after) {
		before = Text.quote(before);
		after = Text.quote(after);
		
		Data d = Encode.unquote(before);
		String s = Encode.quote(d);
		Assert.assertEquals(after, d.toString());//confirm it became what was predicted
	}
	
	private void testUnquoteInvalid(String before) {
		before = Text.quote(before);
		try {
			Data d = Encode.unquote(before);
			Assert.fail();
		} catch (DataException e) {}
	}
}
