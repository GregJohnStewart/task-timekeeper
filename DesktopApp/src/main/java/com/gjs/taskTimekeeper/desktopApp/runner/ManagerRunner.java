package com.gjs.taskTimekeeper.desktopApp.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class ManagerRunner extends ModeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ManagerRunner.class);

	private InputStream is = System.in;

	public ManagerRunner(InputStream is){
		this.is = is;
	}

	@Override
	public void run() {
		LOGGER.info("Running the interactive command line manager mode.");
		//TODO:: do the specified action in the parser
		//TODO:: loop through command line inputs until exit.
	}
}
