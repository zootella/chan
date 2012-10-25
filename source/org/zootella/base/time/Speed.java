package org.zootella.base.time;


public class Speed {
	
	public Speed(long w) {
		this.w = w;
		created = new Now();
	}
	
	private final long w; // Every delay milliseconds, we cycle the boots
	private final Now created;
	private long current;   // The current boot we add objects to
	private long previous;  // The previous boot we just keep around
	
	private long c;
	
	//have add and speed be the same function, it returns the current average speed, you can also give it a distance to add something
	//yeah, that's a cool idea

	public void add(long distance) {
		Now now = new Now();
		
		cycle(now);
		current += distance;
		c = columnNumber(now);
	}
	
	// Help

	/** If it's been long enough since the last time, close and remove the oldest objects we carry. */
	private void cycle(Now now) {
		
		if (c != columnNumber(now)) {
			c = columnNumber(now);
			previous = current;
			current = 0;
		}
	}

	
	private long columnNumber(Now now) {
		
		return (now.time - created.time) / w;
	}
	
	private long sampleTime(Now now) {
		
		long a = (now.time - created.time) % w;
		if (columnNumber(now) != 0) a += w;
		return a;
	}
	
	public long speed() {
		Now now = new Now();
		cycle(now);
		long distance = current + previous;
		long time = sampleTime(now);
		if (time == 0) return 0;
		return distance / time;
	}
	
	
	
}
