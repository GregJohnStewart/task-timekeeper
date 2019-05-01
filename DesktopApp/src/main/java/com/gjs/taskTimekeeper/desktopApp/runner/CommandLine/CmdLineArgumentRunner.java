package com.gjs.taskTimekeeper.desktopApp.runner.CommandLine;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.desktopApp.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.actionDoer.ActionDoer;
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

		TimeManager manager = ManagerIO.loadTimeManager();
		if(manager == null){
			//something bad happened reading data, nothing to do
			return;
		}

		//do action on manager

		try {
			if (ActionDoer.doAction(manager, this.parser)) {
				ManagerIO.saveTimeManager(manager);
			}
		}catch (Exception e){
			//TODO:: handle?
		}

		LOGGER.trace("FINISHED processing argument.");
	}

	public static void showArgHelp(){
		LOGGER.trace("Showing argument help output.");

		System.out.println("Help output:");

		//TODO:: this

	}
}