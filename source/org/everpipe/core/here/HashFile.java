package org.everpipe.core.here;

import org.zootella.data.Data;
import org.zootella.encrypt.hash.HashValve;
import org.zootella.exception.ProgramException;
import org.zootella.file.File;
import org.zootella.file.Open;
import org.zootella.file.OpenTask;
import org.zootella.file.Path;
import org.zootella.file.ReadValve;
import org.zootella.size.Range;
import org.zootella.state.Close;
import org.zootella.state.Model;
import org.zootella.state.Receive;
import org.zootella.state.Update;
import org.zootella.valve.Flow;
import org.zootella.valve.Valve;

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
	private Exception exception;

	/*
	private CenterTask centerTask;
	*/

	@Override public void close() {
		if (already()) return;
		close(openTask);
		close(flow); // Closes the valves in the list
		close(file);
	}
	

	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {
				
				//open the file
				if (openTask == null)
					openTask = new OpenTask(update, new Open(new Path(path), null, Open.read));
				if (done(openTask) && file == null)
					file = openTask.result();
				
				if (file != null && flow == null) {
					
					Range range = new Range(0, file.size());
					
					//hash it
					readValve = new ReadValve(update, file, range);
					hashValve = new HashValve(update, range);
					
					flow = new Flow(update, false, false);
					flow.list.add(readValve);
					flow.list.add(hashValve);
				}
				
				if (flow != null && !flow.closed() && !flow.isEmpty())
					flow.move();
				
				if (flow != null && !flow.closed() && flow.isEmpty()) {
					close(flow);
					
					
				}
					
				
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
	//also time started, speed now, throw the exception that caused us to stop, and so on
	
	
}
