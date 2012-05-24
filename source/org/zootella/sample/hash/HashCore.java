package org.zootella.sample.hash;

import org.zootella.base.encrypt.hash.HashValve;
import org.zootella.base.file.ReadValve;
import org.zootella.base.state.Close;
import org.zootella.base.state.Model;
import org.zootella.base.state.Receive;
import org.zootella.base.state.Update;
import org.zootella.base.valve.Flow;
import org.zootella.base.valve.Valve;

public class HashCore extends Close {

	public HashCore() {
		update = new Update(new MyReceive());
		model = new MyModel();
		
		/*
		readValve = new ReadValve(update, file, range);
		hashValve = new HashValve(update, range);
		
		flow = new Flow(update, false, false);
		flow.list.add(readValve);
		*/
		
		
		
		
		
	}
	
	private final Update update;
	
	private HashFile hashFile;
	
	/*
	private final Flow flow;
	private final ReadValve readValve;
	private final HashValve hashValve;
	*/

	/*
	private CenterTask centerTask;
	*/

	@Override public void close() {
		if (already()) return;
		close(hashFile);
		close(model);
	}
	

	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			
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
		
		if (hashFile != null) close(hashFile);

		hashFile = new HashFile(update, path);
		
		
		
	}
	public void stop() {
		
	}
	public void reset() {
		
	}
	
	

	public final MyModel model;
	public class MyModel extends Model {
		
		public String status1() { return path; }
		public String status2() { return status2; }
		public String status3() { return status3; }
		
		
		
	}
}
