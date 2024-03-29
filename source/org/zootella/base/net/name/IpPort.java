package org.zootella.base.net.name;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.zootella.base.data.Bay;
import org.zootella.base.data.Clip;
import org.zootella.base.data.Data;
import org.zootella.base.data.Number;
import org.zootella.base.data.Split;
import org.zootella.base.data.Text;
import org.zootella.base.exception.DataException;

public class IpPort implements Comparable<IpPort> {
	
	// Look

	/** The Ip address, like 1.2.3.4. */
	public final Ip ip;
	/** The port number, like 80. */
	public final Port port;
	
	// Make
	
	/** Make a new IpPort object with the given Ip address and port number. */
	public IpPort(Ip ip, Port port) { this.ip = ip; this.port = port; }

	// Convert
	
	/** Convert this IpPort into a Java InetSocketAddress object. */
	public InetSocketAddress toInetSocketAddress() { return new InetSocketAddress(ip.toInetAddress(), port.port); }
	
	/** Make a new IpPort with the IP address and port number of the given Java InetSocketAddress object. */
	public IpPort(InetSocketAddress a) { ip = new Ip(a.getAddress()); port = new Port(a.getPort()); }

	// Text
	
	/** Convert this IpPort into text like "1.2.3.4:5". */
	public String toString() { return ip.toString() + ":" + port; }

	/** Make a new IpPort from a String like "1.2.3.4:5". */
	public IpPort(String s) {
		Split<String> split = Text.split(s, ":");
		if (!split.found) throw new DataException();
		ip = new Ip(split.before);
		port = new Port(Number.toInt(split.after, 0, 65535)); // Make sure the port number is 0 through 65535
	}

	// Data

	/**
	 * "123405", the default pattern that describes how the IP and port 1.2.3.4:5 are arranged in 6 bytes of data.
	 * There are 8 patterns you can use:
	 * 
	 * "123405" big endian IP, then big endian port, this default
	 * "123450" big endian IP, then little endian port
	 * "432105" little endian IP, then big endian port
	 * "432150" little endian IP, then little endian port
	 * "051234" big endian port, then big endian IP
	 * "054321" big endian port, then little endian IP
	 * "501234" little endian port, then big endian IP 
	 * "504321" little endian port, then little endian IP
	 */
	public static final String pattern = "123405";
	
	/** Convert this IpPort into 6 bytes of data, 1.2.3.4:5 becomes 01020304 0005. */
	public Data data() { return data(pattern); } // Use the default pattern
	/** Convert this IpPort into 6 bytes of data using a pattern like "123405". */
	public Data data(String pattern) { Bay bay = new Bay(); toBay(bay, pattern); return bay.data(); }
	
	/** Convert this IpPort into 6 bytes added to bay, 1.2.3.4:5 becomes 01020304 0005. */
	public void toBay(Bay bay) { toBay(bay, pattern); } // Use the default pattern
	/** Convert this IpPort into 6 bytes added to bay using a pattern like "123405". */
	public void toBay(Bay bay, String pattern) {
		if (pattern.charAt(0) == '1' || pattern.charAt(0) == '4') {         // IP first
			ip.toBay(bay, Text.start(pattern, 4));
			if (pattern.charAt(4) == '0') Number.toBay(bay, 2, port.port);
			else                          Number.toBayLittle(bay, 2, port.port);
		} else {                                                            // Port first
			if (pattern.charAt(0) == '0') Number.toBay(bay, 2, port.port);
			else                          Number.toBayLittle(bay, 2, port.port);
			ip.toBay(bay, Text.after(pattern, 2));
		}
	}

	/** Make a new IpPort from d which must be 6 bytes, 01020304 0005 becomes 1.2.3.4:5. */
	public IpPort(Data d) { this(d, pattern); } // Use the default pattern
	/** Make a new IpPort from d which must be 6 bytes, use pattern like "123405". */
	public IpPort(Data d, String pattern) {
		if (d.size() != 6) throw new DataException("size");
		if (pattern.charAt(0) == '1' || pattern.charAt(0) == '4') { // IP first
			ip = new Ip(d.start(4), Text.start(pattern, 4));
			if (pattern.charAt(4) == '0') port = new Port(Number.toInt(d.clip(4, 2), 0, 65535));
			else                          port = new Port(Number.toIntLittle(d.clip(4, 2), 0, 65535));
		} else {                                                    // Port first
			if (pattern.charAt(0) == '0') port = new Port(Number.toInt(d.clip(0, 2), 0, 65535));
			else                          port = new Port(Number.toIntLittle(d.clip(0, 2), 0, 65535));
			ip = new Ip(d.after(2), Text.after(pattern, 2));
		}
	}

	// Compare

	@Override public int compareTo(IpPort o) {
		int sort = ip.compareTo(o.ip); // Compare the IP addresses
		if (sort != 0) return sort;    // They're different, sort based on that
		return port.compareTo(o.port); // The IP addresses are the same, sort based on the port numbers
	}

	@Override public boolean equals(Object o) {
		if (o == null || !(o instanceof IpPort)) return false;
		return ip.equals(((IpPort)o).ip) && port.equals(((IpPort)o).port);
	}
	
	@Override public int hashCode() {
		return ip.hashCode() * port.hashCode();
	}
	
	// List

	/** Turn a List of IpPort objects into Data with each in 6 bytes, like "123405123405123405". */
	public static Data data(List<IpPort> list) { Bay bay = new Bay(); toBay(bay, list); return bay.data(); }
	/** Turn a List of IpPort objects into Data with each in 6 bytes, like "123405123405123405", added to bay. */
	public static void toBay(Bay bay, List<IpPort> list) {
		for (IpPort p : list) p.toBay(bay); // Turn each IpPort into 6 bytes of data, and add the bytes to bay
	}
	
	/** Parse data like "123405123405123405", with each IP address and port number in 6 bytes, into a List of IpPort objects. */
	public static List<IpPort> list(Data d) {
		if (d.size() % 6 != 0) throw new DataException("size");
		Clip clip = d.clip(); // Clip around the given data
		List<IpPort> list = new ArrayList<IpPort>();
		while (clip.hasData()) list.add(new IpPort(clip.cut(6))); // Cut 6 bytes from the start of clip until it runs out
		return list;
	}

	/** Turn a List of IpPort objects into a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5". */
	public static String toString(List<IpPort> list) { StringBuffer b = new StringBuffer(); toString(b, list); return b.toString(); }
	/** Turn a List of IpPort objects into a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5", added to the given StringBuffer. */
	public static void toString(StringBuffer b, List<IpPort> list) {
		boolean separate = false;
		for (IpPort p : list) {
			if (separate) b.append(","); // Don't add a "," at the very start
			separate = true;
			b.append(p.toString());
		}
	}

	/** Parse text like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5" into a List of IpPort objects. */
	public static List<IpPort> list(String s) {
		List<String> words = Text.words(s, ",");
		List<IpPort> list = new ArrayList<IpPort>();
		for (String word : words) list.add(new IpPort(word)); // Each word is like "1.2.3.4:5"
		return list;
	}

	/** Turn a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5" into Data like "123405123405123405". */
	public static Data listToData(String s) { Bay bay = new Bay(); listToBay(bay, s); return bay.data(); }
	/** Turn a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5" into Data like "123405123405123405" added to bay. */
	public static void listToBay(Bay bay, String s) { toBay(bay, list(s)); } // Go through list(String)

	/** Turn data like "123405123405123405" into a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5". */
	public static String listToString(Data d) { StringBuffer b = new StringBuffer(); listToString(b, d); return b.toString(); }
	/** Turn data like "123405123405123405" into a String like "1.2.3.4:5,1.2.3.4:5,1.2.3.4:5", added to the given StringBuffer. */
	public static void listToString(StringBuffer b, Data d) { toString(b, list(d)); } // Go through list(Data)
}
