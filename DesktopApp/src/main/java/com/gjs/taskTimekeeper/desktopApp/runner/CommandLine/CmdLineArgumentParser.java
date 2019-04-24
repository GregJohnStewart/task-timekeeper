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
	private Boolean showHelp = null;

	private final String[] argsGotten;
	// receives other command line parameters than options
	@Argument
	private List<String> arguments = new ArrayList<>();
	private final CmdLineParser parser = new CmdLineParser(this);

	public CmdLineArgumentParser(String[] args) throws CmdLineException {
		LOGGER.trace("Parsing command line ops.");
		LOGGER.debug("Command line ops given ({}): {}", args.length, args);
		this.argsGotten = Arrays.copyOf(args, args.length);

		this.parser.parseArgument(this.argsGotten);
	}
}
