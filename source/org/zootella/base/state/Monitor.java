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
	
	
	
	private long loop;
	
	
	
	/** The speed at which pulses are happening right now. */
	private final Speed speed = new Speed(Time.second); // Keep the most recent 1 second of data
	/** The maximum speed we recorded as the program ran. */
	private Average maximumSpeed;
	
	
	/** How many pulses have happened. */
	private long countPulses;
	/** How many pulses have gone over the time limit and quit early. */
	private long countHitLimit;
	/** How long the program has spent inside the pulse function, in milliseconds. */
	private long countTimeInside;
	/** How long the program has spent outside the pulse function, in milliseconds. */
	private long countTimeOutside;
	
	
	
	
	/** true if it's been longer than the delay since the last pulse finished. */
	public boolean ding() {
		return now.expired(Time.delay);
	}
	
	/** A pulse started. */
	public void start() {
		long currentSpeed = speed.add(1, Time.second * 1000); // 1 event, get speed in events per second
		maximumSpeed.add(currentSpeed);
		countPulses++;
		countTimeOutside += now.age(); // Measure how long we were outside
		now = new Now();
		
		loop = 0;
	}
	
	/** Record another loop in the current pulse. */
	public boolean loop() {
		loop++;
		
		
		if (now.expired(Time.delay / 2)) { countHitLimit++; return true; } // Quit early if we're over the time limit
		return false;
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
		s.append("most  | average\r\n");
		s.append(Describe.commas(listSize.maximum())     + "    | " + listSize.averageText()                                                 + " objects/list\r\n");
		s.append(Describe.commas(loopsInPulse.maximum()) + "    | " + loopsInPulse.averageText()                                             + " loops/pulse\r\n");
		s.append(Describe.commas(maximumSpeed.maximum()) +    " | " + average(Time.second * countPulses, countTimeInside + countTimeOutside) + " pulses/second\r\n");
		s.append(Describe.commas(timeInPulse.maximum())  + "    | " + timeInPulse.averageText()                                              + " ms/pulse\r\n");
		s.append("\r\n");
		s.append(percent(countHitLimit, countPulses) + " pulses hit time limit.\r\n");
		s.append(percent(countTimeInside, countTimeInside + countTimeOutside) + " ms time spent pulsing\r\n");
		return s.toString();
	}
	
	
	
	/** Describe a/b like "1.234". */
	private static String average(long a, long b) {
		if (b == 0) return "undefined";
		else return Describe.decimal(1000 * a / b, 3);
	}

	/** Describe a/b like "81% 912/1,123". */
	private static String percent(long a, long b) {
		String s = Describe.commas(a) + "/" + Describe.commas(b);
		if (b != 0) s = Describe.decimal(100000 * a / b, 3) + "% " + s;
		return s;
	}
	
	
	
}
