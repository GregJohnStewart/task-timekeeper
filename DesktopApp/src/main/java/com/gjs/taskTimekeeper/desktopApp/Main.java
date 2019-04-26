package com.gjs.taskTimekeeper.desktopApp;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.config.RunMode;
import com.gjs.taskTimekeeper.desktopApp.runner.GuiRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.ManagerRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.SingleRunner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws CmdLineException {
		LOGGER.info("Starting run of TaskTimekeeper.");
		LOGGER.debug("Input arguments: {}", (Object[]) args);
		Configuration.finalizeConfig(args);

		System.out.println("TaskTimekeeper v" + Configuration.getProperty(ConfigKeys.APP_VERSION, String.class) + " Using lib v" + Configuration.getProperty(ConfigKeys.LIB_VERSION, String.class));
		System.out.println();

		ensureFilesExistWritable();

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

	/**
	 * Ensures files in configuration exist and are accessible.
	 *
	 * If a file is present, accessible, the config value is changed to a File
	 *
	 * If the file cannot be made/ accessed, the config value is changed to null, If the file is marked as required, stops the run.
	 *
	 * TODO:: figure out how to test.
	 */
	private static void ensureFilesExistWritable(){
		Collection<ConfigKeys> fileKeys = ConfigKeys.getKeysThatAreFiles();

		for(ConfigKeys key : fileKeys){
			String fileLocStr = Configuration.getProperty(key, String.class);

			if(fileLocStr == null){
				LOGGER.debug("File location for {} was null.", key);
				if(key.needsFile){
					LOGGER.error("No file location given for {}", key);
					System.err.println("ERROR:: Must specify file for " + key.key);
					System.exit(1);
				}
				continue;
			}

			fileLocStr = replaceFilePlaceholders(fileLocStr);

			File file = new File(fileLocStr);

			try {
				file.getParentFile().mkdirs();
				if(file.createNewFile()){
					LOGGER.info("File did not exist previously, but was created: \"{}\"", file);
				}else{
					LOGGER.info("File already present: \"{}\"", file);
				}
			} catch (IOException e) {
				if(key.needsFile){
					LOGGER.error("Could not create required file \"{}\"", file, e);
					System.err.println("ERROR:: Could not create required file \"" + file.toString() + "\". Error: \""+ e.getMessage() +"\" Exiting.");
					System.exit(1);
				}else{
					LOGGER.warn("Could not create file \"{}\"", file, e);
					System.err.println("WARN::  Could not create file \"" + file.toString() + "\". Error: \""+e.getMessage()+"\"");
					Configuration.setFile(key, null);
					continue;
				}
			}
			//double check file is good to go
			if(file.exists() && file.isFile() && file.canRead() && file.canWrite()){
				LOGGER.debug("Verified file at \"{}\" (exists and is accessible).", file);
				Configuration.setFile(key, file);
				continue;
			}
			//handle if not good to go
			if(key.needsFile){
				LOGGER.error("Could not verify required file \"{}\" exists and has read/write privileges. Exiting.", file);
				System.err.println("ERROR:: Could not verify required file exists and has read/write privileges. Exiting.");
				System.exit(1);
			}else{
				LOGGER.warn("Could not verify required file \"{}\" exists and has read/write privileges. Exiting.", file);
				System.err.println("WARN:: Could not verify file \""+file.toString()+"\" exists and has read/write privileges.");
				Configuration.setFile(key, null);
			}
		}
	}

	private static String replaceFilePlaceholders(String filepath){
		String workingString = filepath;
		//TODO:: document this
		workingString = workingString.replaceAll("\\{HOME}", System.getProperty("user.home"));

		return workingString;
	}
}