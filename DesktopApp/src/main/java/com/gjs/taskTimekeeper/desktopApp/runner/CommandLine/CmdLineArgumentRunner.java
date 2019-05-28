package com.gjs.taskTimekeeper.desktopApp.runner.CommandLine;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.ActionDoer;
import com.gjs.taskTimekeeper.backend.timeParser.TimeParser;
import com.gjs.taskTimekeeper.desktopApp.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public void run(){
		this.run(false);
	}

	public void run(boolean selectLatest) {
		ActionConfig actionConfig = this.parser.getConfig();
		LOGGER.trace("Running argument based on parsed argument: {}", actionConfig);

		if(actionConfig.getQuit()){
			LOGGER.debug("User chose to exit.");
			throw new DoExit();
		}

		if(actionConfig.getShowHelp()) {
			LOGGER.debug("Showing help output.");
			showArgHelp();
			return;
		}

		TimeManager manager = ManagerIO.loadTimeManager();
		if(manager == null){
			//something bad happened reading data, nothing to do. already handled.
			return;
		}

		if(selectLatest){
			LOGGER.trace("Selecting the latest period.");
			ActionDoer.setNewestPeriodAsSelectedQuiet(manager);
		}

		//Do action. If returns true, data was changed.
		if (ActionDoer.doObjAction(manager, actionConfig)) {
			ManagerIO.saveTimeManager(manager);
		}

		LOGGER.trace("FINISHED processing argument.");
	}

	/**
	 * Displays the help output to the terminal.
	 */
	public void showArgHelp(){
		LOGGER.info("Showing argument help output to terminal.");

		System.out.println("Help output:");
		System.out.println("\tFor more detailed information, visit: "); //TODO:: put real URL
		System.out.println();

		this.parser.printUsage();

		TimeParser.outputHelp();
	}
}