package org.zootella.demo.hash;

import org.zootella.base.data.Data;
import org.zootella.base.encrypt.hash.HashValve;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.file.File;
import org.zootella.base.file.Open;
import org.zootella.base.file.OpenTask;
import org.zootella.base.file.Path;
import org.zootella.base.file.ReadValve;
import org.zootella.base.size.Range;
import org.zootella.base.state.Close;
import org.zootella.base.state.Receive;
import org.zootella.base.state.Update;
import org.zootella.base.valve.Flow;

public class HashFile extends Close {

	public HashFile(Update up, String path) {
		this.up = up;
		update = new Update(new MyReceive());

		update.send(); //TODO this is necessary, right?
	}
	
	private final Update up;
	private final Update update;

	private OpenTask openTask;
	private File file;
	private ReadValve readValve;
	private HashValve hashValve;
	private Flow flow;
	private ProgramException exception;

	/*
	private CenterTask centerTask;
	*/

	@Override public void close() {
		if (already()) return;
		close(openTask);
		close(flow); // Closes the valves in the list
		close(file);
		
		up.send();
	}
	

	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {
				
				//open the file
				if (no(openTask))
					openTask = new OpenTask(update, new Open(new Path(path), null, Open.read));
				if (done(openTask) && no(file))
					file = openTask.result();
				
				if (is(file) && no(flow)) {
					
					Range range = new Range(0, file.size());//TODO test that a 0 byte file passes through this correctly, it should
					
					//hash it
					readValve = new ReadValve(update, file, range);
					hashValve = new HashValve(update, range);
					
					flow = new Flow(update, false, false);
					flow.list.add(readValve);
					flow.list.add(hashValve);
				}
				
				if (is(flow))
					flow.move();
				
				up.send();//TODO this is cheap, you should only send one when you know something has changed
				
			} catch (ProgramException e) { exception = e; close(HashFile.this); up.send(); }
		}
	}
	
	private String path;
	private String status1 = "status one";
	private String status2 = "status two";
	private String status3 = "status three";

	
	
	public void open(String path) {
		this.path = path;
	}
	public void start() {
		
	}
	public void stop() {
		
	}
	
	public long sizeHashed() {
		return 0;
	}
	public long sizeTotal() {
		return 0;
	}
	public Data hash() {
		return Data.empty();
	}
	public ProgramException exception() {
		return exception;
	}
	//also time started, speed now, throw the exception that caused us to stop, and so on
	
	
}
