package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.CmdLineException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CmdLineArgumentParserTest {
	
	@Test
	public void badConfigGiven() throws CmdLineException {
		Assertions.assertThrows(CmdLineException.class, ()->{
			new CmdLineArgumentParser(false, new String[]{""});
		});
	}
	
	@Test
	public void getConfig() throws CmdLineException {
		CmdLineArgumentParser parser = new CmdLineArgumentParser(false, new String[0]);
		assertNotNull(parser.getConfig());
	}
	
	@Test
	public void printUsage() throws CmdLineException {
		new CmdLineArgumentParser(false, new String[0]).printUsage();
	}
	
	@Test
	public void getArgsGotten() throws CmdLineException {
		String[] args = new String[0];
		CmdLineArgumentParser parser = new CmdLineArgumentParser(false, args);
		assertArrayEquals(args, parser.getArgsGotten());
	}
}
