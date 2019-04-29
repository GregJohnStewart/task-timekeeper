package com.gjs.taskTimekeeper.desktopApp.runner.CommandLine;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdLineArgumentParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(CmdLineArgumentParser.class);

	@Option(name = "-h", aliases = {"--help"}, usage = "Show this help dialogue.")
	private boolean showHelp = false;

	@Option(name = "-q", aliases = {"q", "--quit", "quit"}, usage = "Exits Management mode.")
	private boolean quit = false;

	@Option(name = "-a", aliases = {"a", "--action", "action"}, usage = "The action to take.")
	private Action action;

	@Option(name = "-o", aliases = {"o", "--object", "object"}, usage = "The type of object to operate on.")
	private KeeperObject objectOperatingOn;

	//Identifiers/ data inputs to add:
	//  current (for periods/ timespans)
	//  name
	//  key/value pair
	//  time/date spans and instants

	@Option(name="t", aliases = {"-t", "--task", "task"}, usage = "The name of the task to operate on.")
	private String taskname;



	private final String[] argsGotten;
	// receives other command line parameters than options
	@Argument
	private List<String> arguments = new ArrayList<>();
	private final CmdLineParser parser = new CmdLineParser(this);

	public CmdLineArgumentParser(boolean allowExtra, String[] args) throws CmdLineException {
		LOGGER.trace("Parsing command line ops.");
		LOGGER.debug("Command line ops given ({}): {}", args.length, args);
		this.argsGotten = Arrays.copyOf(args, args.length);

		try {
			this.parser.parseArgument(this.argsGotten);
		}catch (CmdLineException e){
			//ignore invalid options.
			if(!allowExtra && !e.getMessage().contains("is not a valid option")){
				throw e;
			}
		}
	}

	public CmdLineArgumentParser(String... args) throws CmdLineException {
		this(false, args);
	}

	public Boolean getShowHelp() {
		return showHelp;
	}

	public Boolean getQuit() {
		return quit;
	}

	public Action getAction() {
		return action;
	}

	public KeeperObject getObjectOperatingOn() {
		return objectOperatingOn;
	}
}
