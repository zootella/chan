package org.zootella.pipe.core;

import java.util.ArrayList;
import java.util.List;

import org.zootella.base.state.Close;
import org.zootella.main.Program;
import org.zootella.pipe.core.museum.Pipe;
import org.zootella.pipe.user.ExchangeDialog;
import org.zootella.pipe.user.FolderDialog;
import org.zootella.pipe.user.MuseumDialog;

public class Pipes extends Close {
	
	public Pipes(Program program) {
		this.program = program;
		
		
		pipes = new ArrayList<Pipe>();
		
	}

	public final Program program;
	
	public final List<Pipe> pipes;

	public void make() {

		Pipe pipe = (new MuseumDialog(program)).result();
		if (pipe == null) return;

		new FolderDialog(program, pipe);
		if (!pipe.hasFolder()) { close((Close)pipe); return; }

		new ExchangeDialog(program, pipe);
		if (!pipe.hasAwayCode()) { close((Close)pipe); return; }
		
		pipes.add(pipe);
		program.user.main.fill();
		pipe.go();
	}
	
	public void kill(Pipe pipe) {
		if (pipe == null || !pipes.contains(pipe))
			throw new IllegalArgumentException();
		
		pipes.remove(pipe);
		close((Close)pipe);
		
		program.user.main.fill();
	}

	@Override public void close() {
		if (already()) return;
		
		for (Pipe pipe : pipes)
			close((Close)pipe);
	}
	

}
