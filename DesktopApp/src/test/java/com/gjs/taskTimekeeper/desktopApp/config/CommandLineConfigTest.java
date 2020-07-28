package com.gjs.taskTimekeeper.desktopApp.config;

import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.CmdLineException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommandLineConfigTest {
	
	@Test
	public void test() throws CmdLineException {
		CommandLineConfig cfg = new CommandLineConfig("-h", "--configFile", "textConfig.txt");
		
		assertEquals("textConfig.txt", cfg.getConfigLoc());
		
		cfg = new CommandLineConfig("-p", "-h");
		
		assertNotNull(cfg.getParser());
	}
}
