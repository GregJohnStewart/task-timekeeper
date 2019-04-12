package com.gjs.taskTimekeeper.desktopApp;

import com.gjs.taskTimekeeper.desktopApp.runner.GuiRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.ManagerRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.SingleRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args){
		LOGGER.info("Starting run of TaskTimekeeper.");
		Configuration.readCmdLinesIn(args);
		Configuration.logOutProperties();

		switch (
				Configuration.RunMode.valueOf(
						Configuration.getProperty(Configuration.ConfigKeys.RUN_MODE, String.class)
				)
		){
			case SINGLE:
				new SingleRunner().run();
				break;
			case MANAGEMENT:
				new ManagerRunner().run();
				break;
			case GUI:
				new GuiRunner().run();
				break;
			default:
				throw new IllegalArgumentException("Invalid run mode given.");
		}
	}
}