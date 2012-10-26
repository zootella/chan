package org.zootella.base.time;

/** Make a Speed object, tell it distances traveled or counts when they happen, and get the current speed. */
public class Speed {
	
	/**
	 * Make a new Speed object that can keep track of how fast you're traversing a distance or counting events.
	 * Given a window of 3 * Time.second, the object will keep between 2 and 4 seconds of data to calculate the current speed.
	 */
	public Speed(long window) {
		created = new Now();    // Record that column 0 started now
		width = window * 2 / 3; // Calculate the column width
	}

	/** When this Speed object was created, and the start of column 0. */
	private final Now created;
	/** The width in milliseconds of all the columns in time after that. */
	private final long width;
	
	/** The column index, 0 or more, we last added to. */
	private long column;
	/** The total distance recorded in that column of time. */
	private long current;
	/** The total distance we recorded in the previous column of time. */
	private long previous;

	/** Record that we just traveled the given distance or counted the given number of events. */
	public void distance(long distance) { add(distance, 1); }
	/** Record that we just counted another event. */
	public void count() { add(1, 1); }
	/** Find out how fast we're going right now, 0 or more distance units or events per given time unit, like Time.second. */
	public long speed(long perTimeUnit) { return add(0, perTimeUnit); }
	
	/** Given a distance to add, or 0 to add nothing, calculate our speed right now in the given unit of time. */
	public long add(long distance, long perTimeUnit) {
		
		long age = Time.now() - created.time; // Age of this Speed object
		long columnNow = age / width;         // The column index, 0 or more, the current time places us in now
		long time = age % width;              // How long we've been in the current column
		if (columnNow != 0) time += width;    // After column 0, we also have distances from the previous column in time

		if (column == columnNow) {            // We're still in the same column we last added a distance to, no cycle necessary
		} else if (column + 1 == columnNow) { // Time has moved us into the next column
			previous = current;               // Cycle the totals
			current = 0;
		} else {                              // Time has moved us two or more columns forward
			previous = 0;                     // Zero both totals
			current = 0;
		}

		current += distance; // Add any given distance to the current total
		column = columnNow; // Record the column number we put it in, and the column we cycled to above
		
		if (time < required) return 0; // Avoid reporting huge or inaccurate speeds at the very start
		else return perTimeUnit * (current + previous) / time; // Rate is distance over time
	}
	
	/** Don't report a speed at the very start because we don't have enough data yet. */
	public static long required = Time.second / 10;
}
