package com.gjs.taskTimekeeper.desktopApp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.gjs.taskTimekeeper.desktopApp.config.CommandLineOps.PropertiesOption;

/**
 * Handles the configuration for the running program.
 * <p>
 * How to use:
 * - base properties file and defaults handled automatically
 * - run {@link #finalizeConfig(String[])} to finalize.
 * - access properties using static methods from anywhere
 * <p>
 * Order of configuration sources (lower items can override items above):
 * - Properties file (autogen on build, don't attempt changing)
 * - defaults read in from this and processed
 * (during {@link #finalizeConfig(String[])})
 * - Config file
 * - Environment variables
 * - Command line ops
 */
public class Configuration {
	private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
	/**
	 * The location of the properties file in the resources folder.
	 */
	public static final String PROPERTY_FILE_LOC = "project.properties";

	/**
	 * The properties read from the properties file.
	 */
	private static final Properties PROPERTIES = new Properties();

	/**
	 * If command line args have been read in already.
	 */
	private static boolean finalized = false;

	static {
		readPropertiesFile();
		processDefaults();
	}

	/**
	 * Finalizes the configuration. Only run once.
	 *
	 * @param args
	 */
	public static void finalizeConfig(String[] args) {
		if (finalized) {
			throw new IllegalStateException("Cannot finalize properties twice.");
		}
		finalized = true;

		CommandLineOps ops = new CommandLineOps(args);

		//set config location if overridden
		if (ops.getConfigLoc() != null) {
			PROPERTIES.put(ConfigKeys.CONFIG_FILE, ops.getConfigLoc());
		}

		readFromConfigFile();
		addEnvironmentConfig();
		processCmdLineOps(ops);

		logOutProperties();
	}

	/**
	 * Reads the properties file for configuration
	 */
	private static void readPropertiesFile() {
		LOGGER.trace("Reading properties file for properties.");
		try (InputStream is = Configuration.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_LOC)) {
			if (is == null) {
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
	private static void processDefaults() {
		LOGGER.trace("Processing default configuration values.");
		for (ConfigKeys key : ConfigKeys.getKeysWithDefaultFor()) {
			PROPERTIES.put(
				key.defaultFor.key,
				PROPERTIES.get(key.key)
			);
		}
	}

	/**
	 * Gets configuration from environment config.
	 */
	private static void addEnvironmentConfig() {
		LOGGER.trace("Processing configuration from Environment.");
		Map<String, String> envVars = System.getenv();

		for (ConfigKeys key : ConfigKeys.getKeysWithEnv()) {
			if (envVars.containsKey(key.envVar)) {
				PROPERTIES.put(key.key, envVars.get(key.envVar));
			}
		}
	}

	/**
	 * Reads the configuration file for more properties.
	 * Overrides values held previously.
	 * Does not throw exception if config file not found, but will throw exception of there is an error reading an existing configuration file.
	 */
	private static void readFromConfigFile() {
		LOGGER.trace("Reading config file for properties.");
		try (InputStream is = Configuration.class.getClassLoader().getResourceAsStream(getProperty(ConfigKeys.CONFIG_FILE, String.class))) {
			if (is == null) {
				throw new FileNotFoundException("Input stream was null, assuming file is not present.");
			}
			PROPERTIES.load(is);
		} catch (FileNotFoundException e) {
			LOGGER.warn("Configuration file not found. Cannot read properties in. Error: ", e);
		} catch (IOException e) {
			LOGGER.error("Error reading configuration file. Cannot read configuration in. Error: ", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds the command line ops to the PROPERTIES object.
	 *
	 * @param ops
	 */
	private static void processCmdLineOps(CommandLineOps ops) {
		Class<? extends CommandLineOps> clazz = ops.getClass();

		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			PropertiesOption propertiesOption = field.getAnnotation(PropertiesOption.class);

			if (propertiesOption == null) {
				continue;
			}

			Object value;
			try {
				value = field.get(ops);
			} catch (IllegalAccessException e) {
				LOGGER.error("Could not get value from CommandLineOps. Error: ", e);
				throw new RuntimeException(e);
			}

			if (value != null) {
				PROPERTIES.put(propertiesOption.configKey().key, value);
			}
		}
	}

	/**
	 * Gets all the properties held.
	 *
	 * @return
	 */
	public static Set<Map.Entry<Object, Object>> getAllProperties() {
		return PROPERTIES.entrySet();
	}

	/**
	 * Logs out the properties held.
	 */
	public static void logOutProperties() {
		LOGGER.info("Configuration properties:");

		//TODO:: sort these somehow? Issue with Properties holding <Object, Object>
		getAllProperties().stream().forEach(
			curProp -> LOGGER.info("    {} : {}", curProp.getKey(), curProp.getValue())
		);
	}

	/**
	 * Gets a particular key from the properties, and casts it to the class given.
	 *
	 * @param key The key to get from properties.
	 * @return The property.
	 */
	public static <T> T getProperty(ConfigKeys key, Class<T> clazz) {
		return clazz.cast(PROPERTIES.getProperty(key.key));
	}
}
