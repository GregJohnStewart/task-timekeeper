package com.gjs.taskTimekeeper.desktopApp.runner.CommandLine;

import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdLineArgumentRunner extends ModeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(CmdLineArgumentRunner.class);

	private final CmdLineArgumentParser parser;

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

		//TODO:: do the specified action in the parser.
	}
}
