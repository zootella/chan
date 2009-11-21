package org.zootella.state;

import org.zootella.exception.ProgramException;
import org.zootella.time.Duration;
import org.zootella.time.Now;

public class Result<T> {
	
	public Result(T t, Duration duration, ProgramException exception) {
		this.result = t;
		this.duration = duration;
		this.exception = exception;
	}
	
	public Result(T t, Now start) {
		this.result = t;
		this.duration = new Duration(start);
		this.exception = null;
	}

	public Result(T t, Now start, ProgramException exception) {
		this.result = t;
		this.duration = new Duration(start);
		this.exception = exception;
	}

	private final T result;
	public final Duration duration;
	public final ProgramException exception;
	
	public void check() {
		if (exception != null) throw exception;
		if (result == null) throw new NullPointerException();
	}
	
	public T result() {
		check();
		return result;
	}
}
