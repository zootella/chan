package org.zootella.base.math;

public class Average {
	
	public long n() { return n; }
	public long total() { return total; }
	public long minimum() { return minimum; }
	public long maximum() { return maximum; }
	private long n;
	private long total;
	
	private long minimum;
	private long maximum;
	
	public void add(long value) {
		n++;
		total += value;
		if (value < minimum) minimum = value;
		if (value > maximum) maximum = value;
	}

	public long average() {
		if (n == 0) return 0;
		return total / n;
	}
	public float averageFloat() {
		if (n == 0) return 0;
		return (float)total / (float)n;
	}
	public long averageThousandths() {
		return averageMultiply(1000);
	}
	public long averageMultiply(int multiply) {
		if (n == 0) return 0;
		return multiply * total / n;
	}
	public String averageText() {
		if (n == 0) return "Undefined";
		
		//TODO
		return "";
		
	}

}
