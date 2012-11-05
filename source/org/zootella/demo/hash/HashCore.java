package org.zootella.demo.hash;

import org.zootella.base.state.Close;

public class HashCore extends Close {

	public HashCore() {
		
		/*
		readValve = new ReadValve(update, file, range);
		hashValve = new HashValve(update, range);
		
		flow = new Flow(update, false, false);
		flow.list.add(readValve);
		*/
		
		
		
		
		
	}
	
	
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
	}
	

	@Override public void pulse() {
			
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

		hashFile = new HashFile(path);
		
		
		
	}
	public void stop() {
		
	}
	public void reset() {
		
	}
	
	

		
	public String userStatus1() { return path; }
	public String userStatus2() { return status2; }
	public String userStatus3() { return status3; }
		
		
		
}
