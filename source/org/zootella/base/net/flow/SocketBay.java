package org.zootella.base.net.flow;

import org.zootella.base.data.Bay;
import org.zootella.base.data.Bin;
import org.zootella.base.exception.ProgramException;
import org.zootella.base.net.socket.Socket;
import org.zootella.base.size.Range;
import org.zootella.base.state.Close;

public class SocketBay extends Close {

	/** Put Valve and Bay objects around socket to upload and download. */
	public SocketBay(Socket socket) {
		this.socket = socket;
		
		uploadBay = new Bay();
		downloadBay = new Bay();
		
		uploadValve = new UploadValve(socket, Range.unlimited());
		downloadValve = new DownloadValve(socket, Range.unlimited());
	}
	
	// pull them out and change 'em later, null those you don't want close() to close
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
	
	@Override public void pulse() {
		try {

			uploadValve.stop();
			downloadValve.stop();
			
			if (uploadBay.hasData() && uploadValve.in() != null && uploadValve.in().hasSpace()) {
				uploadValve.in().add(uploadBay);
				soon();
			}

			if (downloadBay.size() < Bin.big && downloadValve.out() != null && downloadValve.out().hasData()) {
				downloadBay.add(downloadValve.out());
				soon();
			}
			
			uploadValve.start();
			downloadValve.start();

		} catch (ProgramException e) { exception = e; close(this); }
	}
	
	/** Add data to upload to this Bay. */
	public Bay upload() { if (exception != null) throw exception; soon(); return uploadBay; } // Pulse soon to notice what the caller adds to upload
	/** Get the data we've downloaded here. */
	public Bay download() { if (exception != null) throw exception; soon(); return downloadBay; }
	
	/** The ProgramException that closed us, or null. */
	public ProgramException exception() { return exception; }
	private ProgramException exception;
}
