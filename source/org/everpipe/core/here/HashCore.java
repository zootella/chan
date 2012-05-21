package org.everpipe.core.here;

import org.zootella.state.Close;
import org.zootella.state.Model;
import org.zootella.state.Receive;
import org.zootella.state.Update;

public class HashCore extends Close {

	public HashCore() {
		update = new Update(new MyReceive());
		model = new MyModel();
		
	}
	
	private final Update update;

	/*
	private CenterTask centerTask;
	*/

	@Override public void close() {
		if (already()) return;
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
		
	}
	public void stop() {
		
	}
	
	

	public final MyModel model;
	public class MyModel extends Model {
		
		public String status1() { return status1; }
		public String status2() { return status2; }
		public String status3() { return status3; }
		
		
		
	}
}
