package com.gjs.taskTimekeeper.desktopApp.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagerRunner extends ModeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ManagerRunner.class);

	@Override
	public void run() {
		LOGGER.info("Running the interactive command line manager mode.");
	}
}
