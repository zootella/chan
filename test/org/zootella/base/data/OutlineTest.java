package org.zootella.base.data;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.zootella.base.data.Data;
import org.zootella.base.data.Outline;
import org.zootella.base.exception.DataException;

public class OutlineTest {

	@Test
	public void test() throws Exception {
		
		Lines l;
		
		l = new Lines();
		l.add("a:");
		l.add("");
		test(l.toString());
		
		l = new Lines();
		l.add("a:");
		l.add("  b:");
		l.add("");
		test(l.toString());

		l = new Lines();
		l.add("a:");
		l.add("b:"); // this is bad because b can't be on the same level
		l.add("");
		testInvalid(l.toString());

		l = new Lines();
		l.add("a:\"hello\"");
		l.add("  b:a\"b"); // the value is bad because it contains a single quote
		l.add("");
		testInvalid(l.toString());
		
		l = new Lines();
		l.add("a:");
		l.add("  b:");
		l.add("  c:");
		l.add("");
		test(l.toString());
		
		l = new Lines();
		l.add("a:");
		l.add("  b:");
		l.add("    c:");
		l.add("");
		test(l.toString());
		
		l = new Lines();
		l.add("a:");
		l.add("  b:");
		l.add("    c:");
		l.add("  d:");
		l.add("");
		test(l.toString());
		
		l = new Lines();
		l.add("a:");
		l.add("  b:");
		l.add("    c:");
		l.add("    d:");
		l.add("");
		test(l.toString());
		
		l = new Lines();
		l.add("a:");
		l.add("  b:");
		l.add("    c:");
		l.add("      d:");
		l.add("");
		test(l.toString());
		
		l = new Lines();
		l.add("a:");
		l.add("  b:");
		l.add("    c:");
		l.add("      d:");
		l.add("  e:");
		l.add("");
		test(l.toString());
		
		l = new Lines();
		l.add("a:");
		l.add("  b:");
		l.add("    c:");
		l.add("      d:");
		l.add("    e:");
		l.add("");
		test(l.toString());

		l = new Lines();
		l.add("a:");
		l.add("  b:");
		l.add("    c:");
		l.add("      d:");
		l.add("      e:");
		l.add("");
		test(l.toString());

		l = new Lines();
		l.add("a:");
		l.add("  b:");
		l.add("    c:");
		l.add("      d:");
		l.add("        e:");
		l.add("");
		test(l.toString());

		l = new Lines();
		l.add("a:");
		l.add("  b:");
		l.add("  c:");
		l.add("    d:");
		l.add("    e:");
		l.add("      f:");
		l.add("      g:");
		l.add("        h:");
		l.add("        i:");
		l.add("          j:");
		l.add("          k:");
		l.add("    l:");
		l.add("      m:");
		l.add("      n:");
		l.add("  o:");
		l.add("    p:");
		l.add("      q:");
		l.add("  r:");
		l.add("  s:");
		l.add("  t:");
		l.add("  u:");
		l.add("    v:");
		l.add("      w:");
		l.add("      x:");
		l.add("    y:");
		l.add("  z:");
		l.add("");
		test(l.toString());
		
		// see how long that one is, in data and in text
		
		Data asText = new Data(l.toString());
		int sizeAsText = asText.size();
		Outline o = Outline.fromText(asText);
		int sizeAsData = o.toData().size();
		System.out.println(sizeAsText + " " + sizeAsData); // 165 to 104
	}
	
	public void testInvalid(String s) throws Exception {
		try {
			test(s);
			fail();
		} catch (DataException e) {}
	}

	public void test(String s) throws Exception {
			
		// text > outline > data > outline > text
		Outline o = Outline.fromText(new Data(s)); // text to outline
		System.out.println(o.toString());
		Data d = o.toData(); // outline to data
		Data d1 = d.copy();
		Outline o2 = new Outline(d); // data to outline
		assertFalse(d.hasData()); // some data left over
		String s2 = o2.toString(); // outline to text
		Data d2 = o2.toData(); // outline to data
		assertTrue(d1.equals(d2)); // corrupted
	}
}
