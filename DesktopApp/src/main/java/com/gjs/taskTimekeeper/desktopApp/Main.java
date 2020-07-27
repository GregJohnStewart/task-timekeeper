package com.gjs.taskTimekeeper.desktopApp;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.config.RunMode;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.CliManagerRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.CliSingleRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.GuiRunner;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;

@Slf4j
public class Main {
	
	public static void main(String[] args) throws CmdLineException {
		log.info("Starting run of TaskTimekeeper.");
		log.debug("Input arguments: {}", (Object[])args);
		DesktopAppConfiguration properties = new DesktopAppConfiguration(args);
		log.info(
			"App version: {} Lib version: {}",
			properties.getProperty(ConfigKeys.APP_VERSION),
			properties.getProperty(ConfigKeys.LIB_VERSION)
		);
		
		RunMode mode = null;
		try {
			mode = RunMode.valueOf(properties.getProperty(ConfigKeys.RUN_MODE).toUpperCase());
		} catch(IllegalArgumentException e) {
			log.error(
				"Bad run mode given ("
					+ properties.getProperty(ConfigKeys.RUN_MODE).toUpperCase()
					+ "). Error: ",
				e
			);
			// deal with later
		}
		
		if(mode != RunMode.SINGLE) {
			System.out.println(
				"TaskTimekeeper v"
					+ properties.getProperty(ConfigKeys.APP_VERSION)
					+ " Using lib versions: "
					+ properties.getProperty(ConfigKeys.LIB_VERSION));
			System.out.println("\tGithub: " + properties.getProperty(ConfigKeys.GITHUB_REPO_URL));
			System.out.println();
		}
		if(mode == null) {
			System.err.println("No run mode given. Exiting.");
			return;
		}
		
		switch(mode) {
		case SINGLE:
			new CliSingleRunner(properties, args).run();
			break;
		case MANAGE:
			new CliManagerRunner(properties).run();
			break;
		case GUI_SWING:
			new GuiRunner(properties).run();
			break;
		default:
			log.error("Invalid run mode given  (" + mode + ").");
			throw new IllegalArgumentException("Invalid run mode given.");
		}
	}
	
	@Deprecated
	public static void forCucumberTestPleaseDontCall() {
		log.info("Don't mind me please. Just for testing that cucumber coverage hits this line.");
	}
}
