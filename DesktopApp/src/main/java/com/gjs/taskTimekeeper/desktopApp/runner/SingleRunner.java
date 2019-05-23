package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.desktopApp.runner.CommandLine.CmdLineArgumentRunner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleRunner extends ModeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(GuiRunner.class);

	private final String args[];

	public SingleRunner(String[] args) {
		this.args = args;
	}

	public void run() {
		LOGGER.info("Running single command mode.");
		System.out.println("Running single command.");

		try {
			CmdLineArgumentRunner runner = new CmdLineArgumentRunner(true, this.args);
			runner.run(true);
		} catch (CmdLineException e) {
			//TODO:: handle better
			LOGGER.error("Exception when running command: ", e);
			System.err.println("Error when running command: " + e.getMessage());
		}
	}
}
