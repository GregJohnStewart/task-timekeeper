package com.gjs.taskTimekeeper.desktopApp.config;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Processes command line options.
 * TODO:: review
 */
@Slf4j
public class CommandLineConfig {
	@Option(
		name = "-h",
		aliases = {"--help"},
		usage = "Show this help dialogue.")
	@PropertiesOption(configKey = ConfigKeys.SINGLE_MODE_HELP)
	private Boolean showHelp = null;
	
	@Option(
		name = "-m",
		aliases = {"--mode"},
		usage = "The mode that this will run with. Default SINGLE.")
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
	/**
	 * The parser to parse the arguments.
	 */
	private final CmdLineParser parser = new CmdLineParser(this);
	
	public CommandLineConfig(String... args) throws CmdLineException {
		log.trace("Parsing command line options for Configuration.");
		log.debug("Command line config given ({}): {}", args.length, args);
		this.argsGotten = Arrays.copyOf(args, args.length);
		
		try {
			this.parser.parseArgument(this.argsGotten);
		} catch(CmdLineException e) {
			// ignore invalid options.
			if(!e.getMessage().contains("is not a valid option")) {
				throw e;
			}
		}
	}
	
	public CmdLineParser getParser() {
		return this.parser;
	}
	
	/**
	 * Annotation to associate a field with a ConfigKey
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface PropertiesOption {
		public ConfigKeys configKey();
	}
	
	/**
	 * Only used for determining config file location and processing things in the right order.
	 *
	 * @return
	 */
	public String getConfigLoc() {
		return configLoc;
	}
}
