package com.gjs.taskTimekeeper.desktopApp.config;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Processes command line options.
 */
public class CommandLineOps {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineOps.class);

	@Option(name = "-h", aliases = {"--help"}, usage = "Show this help dialogue.")
	@PropertiesOption(configKey = ConfigKeys.SINGLE_MODE_HELP)
	private Boolean showHelp = null;

	@Option(name = "-m", aliases = {"--mode"}, usage = "The mode that this will run with. Default SINGLE.")
	@PropertiesOption(configKey = ConfigKeys.RUN_MODE)
	private String runMode = null;

	@Option(name = "--configFile", usage = "Specify a config file to use.")
	@PropertiesOption(configKey = ConfigKeys.CONFIG_FILE)
	private String configLoc = null;

	@Option(name = "--saveFile", usage = "Specify a save file to use.")
	@PropertiesOption(configKey = ConfigKeys.SAVE_FILE)
	private String saveLoc = null;


	private final String[] argsGotten;
	// receives other command line parameters than options
	@Argument
	private List<String> arguments = new ArrayList<>();

	public CommandLineOps(String[] args) {
		LOGGER.trace("Parsing command line ops.");
		LOGGER.debug("Command line ops given ({}): {}", args.length, args);
		this.argsGotten = Arrays.copyOf(args, args.length);

		CmdLineParser parser = new CmdLineParser(this);

		try {
			parser.parseArgument(this.argsGotten);
		} catch (CmdLineException | IllegalArgumentException e) {
			LOGGER.error("Error processing command line options: ", e);
			System.err.println("Error parsing arguments:");
			System.err.println("\t" + e.getMessage());
			System.err.println("");
			// print the list of available options
			System.err.println("Available options:");
			parser.printUsage(System.err);
			System.err.println();
			System.exit(1);
		}
	}

	/**
	 * Only used for determining config file location and processing things in the right order.
	 *
	 * @return
	 */
	public String getConfigLoc() {
		return configLoc;
	}

	/**
	 * Annotation to associate a field with a ConfigKey
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface PropertiesOption {
		public ConfigKeys configKey();
	}
}
