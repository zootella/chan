package org.zootella.base.state;

import org.zootella.base.math.Average;
import org.zootella.base.time.Now;
import org.zootella.base.time.Speed;
import org.zootella.base.time.Time;
import org.zootella.base.user.Describe;

/** The program's Pulse object has a Monitor to record efficiency and performance statistics. */
public class Monitor {

	/** The time when we last entered or left the pulse function. */
	private Now now = new Now();
	
	
	
	private Average listSize = new Average();
	private Average loopsInPulse = new Average();
	private Average timeInPulse = new Average();
	
	
	
	
	
	/** The speed at which pulses are happening right now. */
	private final Speed speed = new Speed(Time.second); // Keep the most recent 1 second of data
	/** The maximum speed we recorded as the program ran. */
	private long maximumSpeed;
	
	/** How many pulses have happened. */
	private long countPulses;
	/** How many loops have happened within all the pulses. */
	private long countLoops;
	/** How many objects we've pulsed within all the loops and pulses. */
	private long countObjects;
	/** How many pulses have gone over the time limit and quit early. */
	private long countHitLimit;
	/** How long the program has spent inside the pulse function, in milliseconds. */
	private long countTimeInside;
	/** How long the program has spent outside the pulse function, in milliseconds. */
	private long countTimeOutside;
	
	
	
	private long loop;
	
	/** true if it's been longer than the delay since the last pulse finished. */
	public boolean ding() {
		return now.expired(Time.delay);
	}
	
	/** A pulse started. */
	public void start() {
		long currentSpeed = speed.add(1, Time.second); // 1 event, get speed in events per second
		if (maximumSpeed < currentSpeed) maximumSpeed = currentSpeed;
		countPulses++;
		countTimeOutside += now.age(); // Measure how long we were outside
		now = new Now();
		
		loop = 0;
	}
	
	/** Record another loop in the current pulse. */
	public boolean loop() {
		loop++;
		
		
		countLoops++;
		if (now.expired(Time.delay / 2)) { countHitLimit++; return true; } // Quit early if we're over the time limit
		return false;
	}
	
	public void object() {
		countObjects++;
	}
	
	/** The pulse ended, the list has n Close objects in it. */
	public void end(int size) {
		
		listSize.add(size);
		loopsInPulse.add(loop);
		
		long inside = now.age();
		countTimeInside += inside; // Measure how long we were inside
		timeInPulse.add(inside);
		
		now = new Now();
	}
	
	

	


	/** Compose text about how efficiently the program has been running. */
	public String composeEfficiency() {
		
		StringBuffer s = new StringBuffer();
		s.append("pulse efficiency:\r\n");

		s.append("\r\n");
		s.append(maximumSpeed + " maximum speed\r\n");
		s.append(countPulses + " pulses\r\n");
		s.append(countLoops + " loops\r\n");
		s.append(countHitLimit + " pulses hit the time limit\r\n");
		s.append(countTimeInside + " milliseconds inside pulse\r\n");
		s.append(countTimeOutside + " milliseconds outside pulse\r\n");
		
		String a = Describe.average(countLoops, countObjects);
		String b = Describe.average(countPulses, countLoops);
		String c = Describe.average(countPulses, countTimeInside);
		String d = Describe.percent(countHitLimit, countPulses);
		String e = Describe.percent(countTimeInside, countTimeInside + countTimeOutside);
		String f = Describe.commas(maximumSpeed);
		
		s.append("\r\n");
		s.append("The average pulse looped up a list of [" + a + "] objects [" + b + "] times.\r\n");
		s.append("The average pulse took [" + c + "] milliseconds, and [" + d + "] hit the time limit.\r\n");
		s.append("The program spent [" + e + "] of its time pulsing.\r\n");
		s.append("The fastest the program pulsed was [" + f + "] pulses per second.\r\n");
		
		s.append("\r\n");
		s.append("List size: " +
				listSize.averageText() + " average, " +
				Describe.commas(listSize.maximum()) + " longest.\r\n");
		s.append("Loops in pulse: " +
				loopsInPulse.averageText() + " average, " +
				Describe.commas(loopsInPulse.maximum()) + " most.\r\n");
		s.append("Pulse frequency: " +
				-1 + " pulse/s average, " +
				-1 + " fastest.\r\n");
		s.append("Pulse duration: " +
				timeInPulse.averageText() + "ms average, " +
				Describe.commas(timeInPulse.maximum()) + "ms longest, " +
				percent(countHitLimit, countPulses) + " pulses hit time limit.\r\n");
		s.append("Time pulsing: " +
				percent(countTimeInside, countTimeInside + countTimeOutside) + "ms total, " +
				-1 + " greatest density.\r\n");
				
		return s.toString();
	}
	
	
	
	/** Given a number of items and the total of all their values, describe the average like "25 average value of 3 items". */
	public static String average(long numberOfItems, long totalValues) {
		if (numberOfItems == 0) return "no average value of 0 items";
		else return (totalValues / numberOfItems) + " average value of " + Describe.commas(numberOfItems) + " items";
	}

	/** Given a portion and the total that it's a part of, describe a percentage like "81% 912/1,123". */
	public static String percent(long portion, long total) {
		String s = Describe.commas(portion) + "/" + Describe.commas(total);
		if (total != 0) s = Describe.decimal(100000 * portion / total, 3) + "% " + s;
		return s;
	}
	
	
	
}
