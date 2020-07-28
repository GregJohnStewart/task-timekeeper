package com.gjs.taskTimekeeper.desktopApp.config;

import com.gjs.taskTimekeeper.desktopApp.config.exception.SetReadOnlyPropertyException;
import com.gjs.taskTimekeeper.desktopApp.config.utils.StringPlaceholder;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.gjs.taskTimekeeper.desktopApp.config.CommandLineConfig.PropertiesOption;

/**
 * Handles the configuration for the running program.
 */
@Slf4j
public class DesktopAppConfiguration {
	/**
	 * The location of the properties file in the resources folder.
	 */
	private static final String PROPERTY_FILE_LOC = "project.properties";
	
	/**
	 * The properties read from the properties file.
	 */
	private Properties properties = new Properties();
	
	public DesktopAppConfiguration(Map<String, String> envVars, CommandLineConfig cmdLine)
		throws CmdLineException {
		this.readPackagedPropertiesFile();
		this.placePackagedDefaults();
		// update user config file location if needed
		if(envVars.get(ConfigKeys.CONFIG_FILE.envVar) != null) {
			this.putProperty(ConfigKeys.CONFIG_FILE, envVars.get(ConfigKeys.CONFIG_FILE.envVar));
		}
		if(cmdLine.getConfigLoc() != null) {
			this.putProperty(ConfigKeys.CONFIG_FILE, cmdLine.getConfigLoc());
		}
		
		readFromUserConfigFile();
		addEnvironmentConfig(envVars);
		replacePlaceholders();
		this.processCmdLineArgs(cmdLine);
	}
	
	public DesktopAppConfiguration(Map<String, String> envVars, String... args)
		throws CmdLineException {
		this(envVars, new CommandLineConfig(args));
	}
	
	public DesktopAppConfiguration(String... args) throws CmdLineException {
		this(System.getenv(), new CommandLineConfig(args));
	}
	
	public DesktopAppConfiguration() throws CmdLineException {
		this(System.getenv(), new CommandLineConfig());
	}
	
	/**
	 * Processes command line arguments, adding them to the properties held.
	 *
	 * @param ops The parsed command line options
	 */
	public void processCmdLineArgs(CommandLineConfig ops) {
		processCmdLineOps(ops);
		replacePlaceholders();
		
		logOutProperties();
	}
	
	/**
	 * Replaces the placeholders held in entries that are not readonly
	 */
	private void replacePlaceholders() {
		for(ConfigKeys key : ConfigKeys.values()) {
			if(!key.readOnly && this.properties.containsKey(key.key)) {
				this.replacePlaceholder(key);
			}
		}
	}
	
	/**
	 * Replaces placeholders at the key given.
	 *
	 * @param key The key to replace placholders in.
	 */
	private void replacePlaceholder(ConfigKeys key) {
		this.properties.setProperty(
			key.key, StringPlaceholder.processPlaceholders(this.getProperty(key)));
	}
	
	/**
	 * Asserts that the key given is not set as read only. Used to check that a property can be
	 * overridden.
	 *
	 * @param key The key to check
	 * @throws SetReadOnlyPropertyException If the key is readonly
	 */
	private void assertKeyIsNotReadOnly(ConfigKeys key) throws SetReadOnlyPropertyException {
		if(key.readOnly) {
			throw new SetReadOnlyPropertyException(
				"Tried to set read only config value. Property attempted to set: " + key.key);
		}
	}
	
	// <editor-fold desc="Properties reading">
	
	/**
	 * Reads the packaged properties file for configuration.
	 */
	private void readPackagedPropertiesFile() {
		log.trace("Reading properties file for properties.");
		try(
			InputStream is =
				DesktopAppConfiguration.class
					.getClassLoader()
					.getResourceAsStream(PROPERTY_FILE_LOC)
		) {
			if(is == null) {
				throw new FileNotFoundException(
					"Input stream was null, assuming built in properties file is not present.");
			}
			properties.load(is);
		} catch(FileNotFoundException e) {
			log.error(
				"Properties file not found. Cannot read build in properties in. Error: ", e);
			throw new RuntimeException(e);
		} catch(IOException e) {
			log.error(
				"Error reading properties file. Cannot read built in properties in. Error: ",
				e
			);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Processes default values.
	 */
	private void placePackagedDefaults() {
		log.trace("Processing default configuration values.");
		for(ConfigKeys key : ConfigKeys.getKeysWithDefaultFor()) {
			this.putProperty(key.defaultFor, this.getProperty(key));
		}
	}
	
	/**
	 * Gets configuration from environment config.
	 */
	private void addEnvironmentConfig(Map<String, String> envVars) {
		log.trace("Processing configuration from Environment.");
		
		for(ConfigKeys key : ConfigKeys.getKeysWithEnv()) {
			if(envVars.containsKey(key.envVar)) {
				this.putProperty(key, envVars.get(key.envVar));
			}
		}
	}
	
	/**
	 * Reads the configuration file for more properties. Overrides values held previously. Does not
	 * throw exception if config file not found, but will throw exception of there is an error
	 * reading an existing configuration file.
	 */
	private void readFromUserConfigFile() {
		log.info("Reading user config file for properties.");
		log.debug("User config file located at: {}", getProperty(ConfigKeys.CONFIG_FILE));
		Properties userFileProps = new Properties();
		try(InputStream is = new FileInputStream(getProperty(ConfigKeys.CONFIG_FILE))) {
			if(is == null) {
				throw new FileNotFoundException(
					"Input stream was null, assuming user's configuration file is not present.");
			}
			userFileProps.load(is);
		} catch(FileNotFoundException e) {
			log.warn(
				"User's configuration file not found. Cannot read properties in from user's config file. Error: ",
				e
			);
		} catch(IOException e) {
			log.error(
				"Error reading user's configuration file. Cannot read user's configuration in. Assumed that it is present, just not readable. Error: ",
				e
			);
			throw new RuntimeException(e);
		}
		
		for(Map.Entry<Object, Object> curProp : userFileProps.entrySet()) {
			this.putProperty(
				ConfigKeys.getKeyOf((String)curProp.getKey()), (String)curProp.getValue());
		}
	}
	
	/**
	 * Adds the command line ops to the PROPERTIES object.
	 *
	 * @param ops The parsed command line ops to add to the properties.
	 */
	private void processCmdLineOps(CommandLineConfig ops) {
		Class<? extends CommandLineConfig> clazz = ops.getClass();
		
		for(Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			PropertiesOption propertiesOption = field.getAnnotation(PropertiesOption.class);
			
			if(propertiesOption == null) {
				continue;
			}
			
			Object value;
			try {
				value = field.get(ops);
			} catch(IllegalAccessException e) {
				log.error("Could not get value from CommandLineConfig. Error: ", e);
				throw new RuntimeException(e);
			}
			
			if(value != null) {
				this.putProperty(propertiesOption.configKey(), (String)value);
			}
		}
	}
	// </editor-fold>
	
	/**
	 * Gets all the properties held.
	 *
	 * @return The entries held in the properties held.
	 */
	public Set<Map.Entry<Object, Object>> getAllProperties() {
		return properties.entrySet();
	}
	
	/**
	 * Logs out the properties held.
	 */
	public void logOutProperties() {
		log.info("Configuration properties:");
		
		// TODO:: sort these somehow? Issue with Properties holding <Object, Object>
		getAllProperties().stream()
						  .forEach(
							  curProp->
								  log.info("    {} : {}", curProp.getKey(), curProp.getValue()));
	}
	
	/**
	 * Sets a property in the configuration. Ensures the config property is read only and resolves
	 * placeholders.
	 *
	 * @param key   The key of the property to set.
	 * @param value The value to give the property
	 * @return The old value that was held at the key.
	 */
	public String putProperty(ConfigKeys key, String value) {
		assertKeyIsNotReadOnly(key);
		String oldVal = (String)properties.put(key.key, value);
		this.replacePlaceholder(key);
		
		return oldVal;
	}
	
	/**
	 * Gets a particular key from the properties, and casts it to the class given.
	 *
	 * @param key The key to get from properties.
	 * @return The property.
	 */
	public String getProperty(ConfigKeys key) {
		return properties.getProperty(key.key);
	}
}
