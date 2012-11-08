package org.zootella.base.state;

import org.zootella.base.exception.ProgramException;
import org.zootella.base.time.Duration;
import org.zootella.base.time.Now;

/*
 * TODO
 * split this into two simpler objects
 * 
 * Result<Type> has a Type which cannot be null, and a Duration of how long it took
 * it has to be successful, there can be no null or exception
 * 
 * Outcome<Type> has a Duration of how long it took, and either a non-null Type, or an exception that prevented it
 * the constructor makes sure it can't have both, one or the other only
 * call outcome.result(), and you get the object or get thrown the exception
 * call outcome.exception() if you want to receive it, not get it thrown up at you
 * 
 * then, look at Move and see if you can use Result or Outcome there
 */
public class Result<Type> {
	
	public Result(Type t, Duration duration, ProgramException exception) {
		this.result = t;
		this.duration = duration;
		this.exception = exception;
	}
	
	public Result(Type t, Now start) {
		this.result = t;
		this.duration = new Duration(start);
		this.exception = null;
	}

	public Result(Type t, Now start, ProgramException exception) {
		this.result = t;
		this.duration = new Duration(start);
		this.exception = exception;
	}

	private final Type result;
	public final Duration duration;
	public final ProgramException exception;
	
	public void check() {
		if (exception != null) throw exception;
		if (result == null) throw new NullPointerException();
	}
	
	public Type result() {
		check();
		return result;
	}
}
