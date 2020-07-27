package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;

/**
 * Runner for a single cli input. TODO:: test
 */
@Slf4j
public class CliSingleRunner extends ModeRunner {
	
	private final String[] args;
	
	public CliSingleRunner(DesktopAppConfiguration config, String... args) {
		super(config);
		this.args = args;
	}
	
	public void run() {
		log.info("Running in single command mode.");
		// System.out.println("Running single command.");
		
		try {
			CmdLineArgumentRunner runner = new CmdLineArgumentRunner(this.config, true, this.args);
			runner.getManagerIO().setAutoSave(true);
			runner.run(true);
		} catch(CmdLineException e) {
			log.error("Exception when running command: ", e);
			System.err.println("Error when running command: " + e.getMessage());
		}
	}
}
