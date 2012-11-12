package org.zootella.base.state;

import org.zootella.base.time.Now;
import org.zootella.base.time.Speed;
import org.zootella.base.time.Time;
import org.zootella.base.user.Describe;

/** The program's Pulse object has a Monitor to record efficiency and performance statistics. */
public class Monitor {
	
	public boolean ding() {
		return now.expired(Time.delay);
	}
	
	public void start() {
		long currentSpeed = speed.add(1, Time.second); // 1 event, get speed in events per second
		if (maximumSpeed < currentSpeed) maximumSpeed = currentSpeed; //TODO maybe skip middle if current speed is too high?
		countPulses++;
		countTimeOutside += now.age(); // Measure how long we were outside
		now = new Now();
	}
	
	public boolean loop() {
		countLoops++;
		if (now.expired(Time.delay / 2)) { countHitLimit++; return true; } // Quit early if we're over the time limit
		return false;
	}
	
	public void object() {
		countObjects++;
	}
	
	public void end(int size) {
		countTimeInside += now.age(); // Measure how long we were inside
		now = new Now();
	}

	

	//TODO change some of these to your new Average object

	/** The time when we last entered or left the pulse function. */
	private Now now = new Now();
	
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

	/** Compose text about how efficiently the program has been running. */
	public String composeEfficiency() {

		//TODO add
		//longest pulse
		//longest list
		
		StringBuffer s = new StringBuffer();
		s.append("pulse efficiency:\r\n");

		s.append(maximumSpeed + " maximum speed\r\n");
		s.append(countPulses + " pulses\r\n");
		s.append(countLoops + " loops\r\n");
		s.append(countObjects + " objects pulsed\r\n");
		s.append(countHitLimit + " pulses hit the time limit\r\n");
		s.append(countTimeInside + " milliseconds inside pulse\r\n");
		s.append(countTimeOutside + " milliseconds outside pulse\r\n");
		
		s.append("The average pulse looped up a list of [" + Describe.average(countLoops, countObjects) + "] objects [" + Describe.average(countPulses, countLoops) + "] times.\r\n");
		s.append("The average pulse took [" + Describe.average(countPulses, countTimeInside) + "] milliseconds, and [" + Describe.percent(countHitLimit, countPulses) + "] hit the time limit.\r\n");
		s.append("The program spent [" + Describe.percent(countTimeInside, countTimeInside + countTimeOutside) + "] of its time pulsing.\r\n");
		s.append("The fastest the program pulsed was [" + Describe.commas(maximumSpeed) + "] pulses per second.\r\n");
		return s.toString();
	}
	
	
}
