package com.gjs.taskTimekeeper.desktopApp;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.config.RunMode;
import com.gjs.taskTimekeeper.desktopApp.managerIO.LocalFile;
import com.gjs.taskTimekeeper.desktopApp.runner.GuiRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.ManagerRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.SingleRunner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws CmdLineException {
		LOGGER.info("Starting run of TaskTimekeeper.");
		LOGGER.debug("Input arguments: {}", (Object[]) args);
		Configuration.finalizeConfig(args);

		System.out.println("TaskTimekeeper v" + Configuration.getProperty(ConfigKeys.APP_VERSION, String.class) + " Using lib v" + Configuration.getProperty(ConfigKeys.LIB_VERSION, String.class));
		System.out.println();

		LocalFile.ensureFilesExistWritable();

		switch (
			RunMode.valueOf(
				Configuration.getProperty(ConfigKeys.RUN_MODE, String.class)
			)
		) {
			case SINGLE:
				new SingleRunner(args).run();
				break;
			case MANAGEMENT:
				new ManagerRunner().run();
				break;
			case GUI:
				new GuiRunner().run();
				break;
			default:
				LOGGER.error("Invalid run mode given.");
				throw new IllegalArgumentException("Invalid run mode given.");
		}
	}

}