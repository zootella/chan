package org.zootella.base.data;

public class Lines {
	
	public Lines() {
		lines = new StringBuffer();
	}
	
	private final StringBuffer lines;
	
	public void add(String s) {
		lines.append(s);
		lines.append("\r\n");
	}
	
	@Override public String toString() {
		return lines.toString();
	}

}
