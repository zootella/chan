package org.zootella.demo.hash;

import org.zootella.base.encrypt.hash.HashValve;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.file.File;
import org.zootella.base.file.Open;
import org.zootella.base.file.OpenTask;
import org.zootella.base.file.Path;
import org.zootella.base.file.ReadValve;
import org.zootella.base.size.Range;
import org.zootella.base.state.Close;
import org.zootella.base.valve.Flow;

public class HashFile extends Close {

	public HashFile(String path) {
		this.path = path;
	}
	
	private String path;
	private OpenTask openTask;
	private File file;
	private ReadValve readValve;
	private HashValve hashValve;
	private Flow flow;
	private ProgramException exception;

	@Override public void close() {
		if (already()) return;
		close(openTask);
		close(flow); // Closes the valves in the list
		close(file);
	}
	

	@Override public void pulse() {
		try {
			
			if (no(openTask)) {
				openTask = new OpenTask(new Open(new Path(path), null, Open.read));
			}
			if (done(openTask) && no(file)) {
				file = openTask.result();
			}
			
			if (is(file) && no(flow)) {
				
				Range range = new Range(0, file.size());//TODO test that a 0 byte file passes through this correctly, it should
				
				readValve = new ReadValve(file, range);
				hashValve = new HashValve(range);
				
				flow = new Flow(false, false);
				flow.list.add(readValve);
				flow.list.add(hashValve);
			}
			
			if (is(flow)) {
				flow.move();
			}
			
			if (flow != null && flow.isEmpty()) {
				close(this);
			}
			
		} catch (ProgramException e) { exception = e; close(this); }
	}
	
	//commands
	
	public void start() {
		
	}
	public void stop() {
		
	}
	
	//status and result
	
	public long sizeHashed() {
		try {
			return hashValve.meter().done();
		} catch (NullPointerException e) { return -1; }
	}
	public long sizeTotal() {
		try {
			return hashValve.meter().range().size;
		} catch (NullPointerException e) { return -1; }
	}
	public String hash() {
		try {
			return hashValve.hash.value().data.base16();
		} catch (NullPointerException e) { return ""; }
	}
	public ProgramException exception() {
		return exception;
	}
	//also time started, speed now, throw the exception that caused us to stop, and so on
	
	
}
