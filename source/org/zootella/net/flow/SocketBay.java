package org.zootella.net.flow;

import org.zootella.data.Bay;
import org.zootella.data.Bin;
import org.zootella.exception.ProgramException;
import org.zootella.net.socket.Socket;
import org.zootella.size.Range;
import org.zootella.state.Close;
import org.zootella.state.Receive;
import org.zootella.state.Update;

public class SocketBay extends Close {

	/** Put Valve and Bay objects around socket to upload and download. */
	public SocketBay(Update up, Socket socket) {
		this.up = up;
		this.socket = socket;
		
		uploadBay = new Bay();
		downloadBay = new Bay();
		
		update = new Update(new MyReceive());
		uploadValve = new UploadValve(update, socket, Range.unlimited());
		downloadValve = new DownloadValve(update, socket, Range.unlimited());
		update.send();
	}
	
	private final Update update;
	
	// pull them out and change 'em later, null those you don't want close() to close
	public Update up;
	public Socket socket;
	public UploadValve uploadValve;
	public DownloadValve downloadValve;
	public Bay uploadBay;
	public Bay downloadBay;

	@Override public void close() {
		if (already()) return;
		close(uploadValve);
		close(downloadValve);
		close(socket);
	}
	
	private class MyReceive implements Receive {
		public void receive() {
			if (closed()) return;
			try {

				uploadValve.stop();
				downloadValve.stop();
				
				if (uploadBay.hasData() && uploadValve.in() != null && uploadValve.in().hasSpace()) {
					uploadValve.in().add(uploadBay);
					up.send();
				}

				if (downloadBay.size() < Bin.big && downloadValve.out() != null && downloadValve.out().hasData()) {
					downloadBay.add(downloadValve.out());
					up.send();
				}
				
				uploadValve.start();
				downloadValve.start();

			} catch (ProgramException e) { exception = e; close(SocketBay.this); }
		}
	}
	
	/** Add data to upload to this Bay. */
	public Bay upload() { if (exception != null) throw exception; update.send(); return uploadBay; } // Send update to notice what the caller adds to upload
	/** Get the data we've downloaded here. */
	public Bay download() { if (exception != null) throw exception; update.send(); return downloadBay; }
	
	/** The ProgramException that closed us, or null. */
	public ProgramException exception() { return exception; }
	private ProgramException exception;
}
