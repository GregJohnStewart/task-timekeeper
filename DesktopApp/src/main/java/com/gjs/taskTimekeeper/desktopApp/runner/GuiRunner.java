package com.gjs.taskTimekeeper.desktopApp.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiRunner extends ModeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(GuiRunner.class);

	@Override
	public void run() {
		LOGGER.info("Running the GUI mode.");
	}
}
