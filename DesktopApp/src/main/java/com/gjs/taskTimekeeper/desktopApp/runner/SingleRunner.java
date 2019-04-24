package com.gjs.taskTimekeeper.desktopApp.runner;

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
	}
}
