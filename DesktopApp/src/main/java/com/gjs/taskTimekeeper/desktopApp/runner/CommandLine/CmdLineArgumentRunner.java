package com.gjs.taskTimekeeper.desktopApp.runner.CommandLine;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.actionDoer.ActionDoer;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CmdLineArgumentRunner extends ModeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(CmdLineArgumentRunner.class);

	private final CmdLineArgumentParser parser;

	//TODO:: add saveFile member to make testing easier

	public CmdLineArgumentRunner(CmdLineArgumentParser parser) {
		this.parser = parser;
	}

	public CmdLineArgumentRunner(boolean allowExtra, String... args) throws CmdLineException {
		this(new CmdLineArgumentParser(allowExtra, args));
	}

	public CmdLineArgumentRunner(boolean allowExtra, String inputString) throws CmdLineException {
		//TODO:: parse inputString as array....better
		this(allowExtra, inputString.split(" "));
	}


	@Override
	public void run() {
		LOGGER.trace("Running argument based on parsed argument: {}", parser);

		if(this.parser.getQuit()){
			LOGGER.debug("User chose to exit.");
			throw new DoExit();
		}

		if (this.parser.getShowHelp()) {
			LOGGER.debug("Showing help output.");
			showArgHelp();
			return;
		}

		final Action action = this.parser.getAction();
		if(action == null){
			LOGGER.warn("No action given.");
			System.out.println("No action given. Doing nothing.");
			return;
		}

		TimeManager manager = null;

		//TODO:: read/write with gip compression? https://stackoverflow.com/questions/40931524/writing-to-json-gz-file-using-jackson-object-mapper
		LOGGER.trace("Reading in saved data.");
		try(
			InputStream is = new FileInputStream(
				Configuration.getProperty(ConfigKeys.SAVE_FILE, File.class)
			)
		) {
			manager = TimeManager.MAPPER.readValue(is, TimeManager.class);
		} catch (MismatchedInputException e){
			LOGGER.debug("Empty save file. Starting with new manager.");
			System.out.println("New data initiallized.");
			manager = new TimeManager();
		} catch (IOException e) {
			//TODO:: what happens when empty file? Will it throw this exception?
			LOGGER.error("FAILED to read in saved data: ", e);
			System.err.println("FAILED to read save data in. Error: " + e.getMessage());
			return;
		}
		LOGGER.trace("Done reading in save data.");
		//read in files for use.

		//do action on manager

		ActionDoer.doAction(manager, this.parser);

		LOGGER.trace("Writing out changed data.");
		try(
			OutputStream os = new FileOutputStream(
				Configuration.getProperty(ConfigKeys.SAVE_FILE, File.class)
			)
			){
			TimeManager.MAPPER.writeValue(os, manager);
		} catch (IOException e) {
			LOGGER.error("FAILED to write changes to save file. Error: ", e);
			System.err.println("FAILED to write changes to save file. File might have become corrupt or failed to save changes.");
			System.err.println("\tError: " + e.getMessage());
		}
		LOGGER.trace("Done writing out changed data.");

		LOGGER.trace("FINISHED processing argument.");
	}

	public static void showArgHelp(){
		LOGGER.trace("Showing argument help output.");

		System.out.println("Help output:");

		//TODO:: this

	}
}