package com.gjs.taskTimekeeper.desktopApp;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Configuration {
	private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
	/** The location of the properties file in the resources folder. */
	public static final String PROPERTY_FILE_LOC = "project.properties";

	/** The properties read from the properties file.*/
	private static final Properties PROPERTIES = new Properties();

	/** If command line args have been read in already. */
	private static boolean readCmdLinesIn = false;

	static {
		readPropertiesFile();
		processDefaults();
		addEnvironmentConfig();
	}

	/**
	 * Reads the properties file for configuration
	 */
	private static void readPropertiesFile(){
		try(InputStream is = Configuration.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_LOC)){
			if(is == null){
				throw new FileNotFoundException("Input stream was null, assuming file is not present.");
			}
			PROPERTIES.load(is);
		} catch (FileNotFoundException e) {
			LOGGER.error("Properties file not found. Cannot read properties in. Error: ", e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOGGER.error("Error reading properties file. Cannot read properties in. Error: ", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Processes default values.
	 */
	private static void processDefaults(){
		for(ConfigKeys key : ConfigKeys.getKeysWithDefaultFor()){
			PROPERTIES.put(
					key.defaultFor.key,
					PROPERTIES.get(key.key)
			);
		}
	}

	/**
	 * Gets configuration from environment config.
	 */
	private static void addEnvironmentConfig(){
		Map<String, String> envVars = System.getenv();

		for(ConfigKeys key : ConfigKeys.getKeysWithEnv()){
			if(envVars.containsKey(key.envVar)){
				PROPERTIES.put(key.key, envVars.get(key.envVar));
			}
		}
	}

	/**
	 * Processes command line arguments.
	 * @param args The arguments to process.
	 */
	public static void readCmdLinesIn(String[] args){
		if(readCmdLinesIn){
			throw new IllegalStateException("Cannot read command line args twice.");
		}
		readCmdLinesIn = true;

		CommandLineOps ops = new CommandLineOps(args);

	}

	/**
	 * Keys for accessing configuration.
	 */
	public enum ConfigKeys {
		//basic config keys, from project.properties
		APP_NAME("app.name"),
		APP_VERSION("app.version"),
		APP_BUILDTIME("app.buildtime"),
		LIB_VERSION("lib.version"),
		//Other config
		SAVE_FILE("saveFile", "TKPR_SAVE_FILE"),
		RUN_MODE("run.mode", "TKPR_MODE"),
		//Running config, for single mode
		SINGLE_MODE_(""),
		//default value
		DEFAULT_SAVE_FILE("default.saveFile", SAVE_FILE),
		DEFAULT_RUN_MODE("default.runMode", RUN_MODE);

		public final String key;
		public final String envVar;
		public final ConfigKeys defaultFor;

		ConfigKeys(String key){
			this.key = key;
			this.envVar = null;
			this.defaultFor = null;
		}
		ConfigKeys(String key, String envVar){
			this.key = key;
			this.envVar = envVar;
			this.defaultFor = null;
		}
		ConfigKeys(String key, ConfigKeys defaultFor){
			this.key = key;
			this.envVar = null;
			this.defaultFor = defaultFor;
		}

		public static Collection<ConfigKeys> getKeysWithEnv(){
			List<ConfigKeys> keys = new ArrayList<>();
			for(ConfigKeys key : ConfigKeys.values()){
				if(key.envVar != null){
					keys.add(key);
				}
			}
			return keys;
		}

		public static Collection<ConfigKeys> getKeysWithDefaultFor(){
			List<ConfigKeys> keys = new ArrayList<>();
			for(ConfigKeys key : ConfigKeys.values()){
				if(key.defaultFor != null){
					keys.add(key);
				}
			}
			return keys;
		}
	}

	public enum RunMode {
		SINGLE,
		MANAGEMENT,
		GUI;
	}

	/**
	 * Private class for processing command line ops.
	 */
	private static class CommandLineOps {
		private final Logger LOGGER = LoggerFactory.getLogger(CommandLineOps.class);

		private final String[] argsGotten;

		@Option(name = "-m", aliases = {"--mode"}, usage = "The mode that this will run with. Default SINGLE.")
		private RunMode runMode = null;

		@Option(name = "-h", aliases = {"--help"}, usage = "Show this help dialogue.")
		private boolean showHelp = false;


		// receives other command line parameters than options
		@Argument
		private List<String> arguments = new ArrayList<>();

		public CommandLineOps(String[] args) {
			this.argsGotten = Arrays.copyOf(args, args.length);

			CmdLineParser parser = new CmdLineParser(this);

			try {
				// parse the arguments.
				parser.parseArgument(this.argsGotten);

				//if (this.argsGotten.length == 0) {
				//	throw new IllegalArgumentException("No arguments given");
				//}
				if (this.showHelp) {
					System.out.println("Available options:");
					parser.printUsage(System.out);
					System.exit(0);
				}
			} catch (CmdLineException | IllegalArgumentException e) {
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
	}

	/**
	 * Gets all the properties held.
	 * @return
	 */
	public static Set<Map.Entry<Object, Object>> getAllProperties(){
		return PROPERTIES.entrySet();
	}

	/**
	 * Logs out the properties held.
	 */
	public static void logOutProperties(){
		LOGGER.info("Configuration properties:");

		for(Map.Entry<Object, Object> curProp : getAllProperties()){
			LOGGER.info("    {} : {}", curProp.getKey(), curProp.getValue());
		}
	}

	/**
	 * Gets a particular key from the properties, and casts it to the class given.
	 * @param key The key to get from properties.
	 * @return The property.
	 */
	public <T> T getProperty(ConfigKeys key, Class<T> clazz){
		return clazz.cast(PROPERTIES.getProperty(key.key));
	}
}
