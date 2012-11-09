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
		//what if n is zero
		return total / n;
	}
	public float average() {
		return (float)total / (float)n;
	}
	public long averageThousandths() {
		return 1000 * total / n;
	}
	public String averageText() {
		
	}

}
