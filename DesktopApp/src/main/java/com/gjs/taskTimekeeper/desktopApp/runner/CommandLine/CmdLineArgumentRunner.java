package com.gjs.taskTimekeeper.desktopApp.runner.CommandLine;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.ActionDoer;
import com.gjs.taskTimekeeper.desktopApp.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdLineArgumentRunner extends ModeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(CmdLineArgumentRunner.class);

	private final ActionConfig actionConfig;

	//TODO:: add saveFile member to make testing easier

	public CmdLineArgumentRunner(CmdLineArgumentParser parser) {
		this.actionConfig = parser.getConfig();
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
		LOGGER.trace("Running argument based on parsed argument: {}", actionConfig);

		if(this.actionConfig.getQuit()){
			LOGGER.debug("User chose to exit.");
			throw new DoExit();
		}

		if (this.actionConfig.getShowHelp()) {
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
		if (ActionDoer.doObjAction(manager, this.actionConfig)) {
			ManagerIO.saveTimeManager(manager);
		}

		LOGGER.trace("FINISHED processing argument.");
	}



	public static void showArgHelp(){
		LOGGER.trace("Showing argument help output.");

		System.out.println("Help output:");

		//TODO:: this

	}
}