package org.zootella.base.math;

import org.zootella.base.user.Describe;

/** Calculate the average of a number of vales as they are produced. */
public class Average {

	/** How many values we have, 0 before you add one. */
	public long n() { return n; }
	private long n;

	/** The total sum of all of the given values. */
	public long total() { return total; }
	private long total;

	/** The smallest value we have seen, 0 before we have any values. */
	public long minimum() { return minimum; }
	private long minimum;
	
	/** The largest value we have seen, 0 before we have any values. */
	public long maximum() { return maximum; }
	private long maximum;
	
	/** Record a new value to make it a part of this average. */
	public void add(long value) {
		n++; // Count another value
		total += value; // Add the value to our total
		if (n == 1 || value < minimum) minimum = value; // First or smallest value
		if (n == 1 || value > maximum) maximum = value; // First or largest value
	}

	/** The current average, rounded down to a whole number, 0 before we have any values. */
	public long average() {
		if (n == 0) return 0;
		return total / n;
	}

	/** The current average, 0 before we have any values. */
	public float averageFloat() {
		if (n == 0) return 0;
		return (float)total / (float)n;
	}
	
	/** The current average in thousandths, given 4, 5, and 6, the average is 5000. */
	public long averageThousandths() { return averageMultiply(1000); }
	/** The current average multiplied by the given number. */
	public long averageMultiply(long multiply) {
		if (n == 0) return 0;
		return multiply * total / n;
	}

	/** Text that describes the current average, like "5.000", "Undefined" before we have any values. */
	public String averageText() {
		if (n == 0) return "Undefined";
		return Describe.thosandths(averageThousandths());
	}
}
