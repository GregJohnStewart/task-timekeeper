package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

import static org.junit.Assert.assertArrayEquals;

public class CmdLineArgumentRunnerTest {

	@Test
	public void canSplitProperly() throws CmdLineException {
		CmdLineArgumentRunner runner = new CmdLineArgumentRunner(true, "hello world");
		assertArrayEquals(new String[]{"hello", "world"}, runner.getParser().getArgsGotten());

		runner = new CmdLineArgumentRunner(true, "hello\\ there\\ world");
		assertArrayEquals(new String[]{"hello there world"}, runner.getParser().getArgsGotten());

		runner = new CmdLineArgumentRunner(true, "hello\\ there, world");
		assertArrayEquals(new String[]{"hello there,", "world"}, runner.getParser().getArgsGotten());
	}

}