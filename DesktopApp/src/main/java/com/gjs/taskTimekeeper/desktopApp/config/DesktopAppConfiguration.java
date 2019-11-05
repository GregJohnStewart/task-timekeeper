package com.gjs.taskTimekeeper.desktopApp.config;

import static com.gjs.taskTimekeeper.desktopApp.config.CommandLineConfig.PropertiesOption;

import com.gjs.taskTimekeeper.desktopApp.config.exception.SetReadOnlyPropertyException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles the configuration for the running program. */
public class DesktopAppConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(DesktopAppConfiguration.class);
    /** The location of the properties file in the resources folder. */
    private static final String PROPERTY_FILE_LOC = "project.properties";

    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-YYYY");
    private static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH-mm");
    private static DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH-mm_dd-MM-YYYY");

    /** Default global configuration. Needs to have the command line ops handed to it. */
    public static final DesktopAppConfiguration GLOBAL_CONFIG =
            new DesktopAppConfiguration(); // TODO:: setup properly

    /** The properties read from the properties file. */
    private Properties properties = new Properties();

    /** Reads in all configuration besides command line args. */
    public DesktopAppConfiguration() {
        this.readPackagedPropertiesFile();
        this.placePackagedDefaults();
        // TODO:: only read in user config file location instead of all env config
        addEnvironmentConfig(); // read env config in before to have user config file set if passed
        // in.
        readFromUserConfigFile();
        addEnvironmentConfig(); // read in after as these values override user config file
        replacePlaceholders();
    }

    /**
     * Calls the {@link #DesktopAppConfiguration() base constructor} and adds config form the
     * arguments given.
     *
     * @param args The command line args to parse and use.
     * @throws CmdLineException If something went wrong with parsing the arguments
     */
    public DesktopAppConfiguration(String... args) throws CmdLineException {
        this();
        this.processCmdLineArgs(args);
    }

    /**
     * Processes command line arguments, adding them to the properties held.
     *
     * @param ops The parsed command line options
     */
    public void processCmdLineArgs(CommandLineConfig ops) {
        // set config location if overridden
        if (ops.getConfigLoc() != null
                && !ops.getConfigLoc().equals(properties.getProperty(ConfigKeys.CONFIG_FILE.key))) {
            properties.put(ConfigKeys.CONFIG_FILE, ops.getConfigLoc());
            readFromUserConfigFile();
            addEnvironmentConfig(); // reread to preserve their overriding of config file
        }

        processCmdLineOps(ops);
        replacePlaceholders();

        logOutProperties();
    }

    /**
     * Processes command line arguments, adding them to the properties held.
     *
     * @param args The arguments passed in
     * @throws CmdLineException
     */
    public void processCmdLineArgs(String... args) throws CmdLineException {
        processCmdLineArgs(new CommandLineConfig(args));
    }

    /** Replaces the placeholders held in entries that are not readonly */
    private void replacePlaceholders() {
        for (ConfigKeys key : ConfigKeys.values()) {
            if (!key.readOnly && this.properties.contains(key.key)) {
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
        String curProperty = this.getProperty(key);

        curProperty = curProperty.replaceAll("\\{HOME}", System.getProperty("user.home"));
        curProperty = curProperty.replaceAll("\\{DATE}", LocalDate.now().format(DATE_FORMATTER));
        curProperty = curProperty.replaceAll("\\{TIME}", LocalDate.now().format(TIME_FORMATTER));
        curProperty =
                curProperty.replaceAll("\\{DATETIME}", LocalDate.now().format(DATETIME_FORMATTER));

        this.properties.setProperty(key.key, curProperty);
    }

    /**
     * Asserts that the key given is not set as read only. Used to check that a property can be
     * overridden.
     *
     * @param key The key to check
     * @throws SetReadOnlyPropertyException If the key is readonly
     */
    private void assertKeyIsNotReadOnly(ConfigKeys key) throws SetReadOnlyPropertyException {
        if (key.readOnly) {
            throw new SetReadOnlyPropertyException(
                    "Tried to set read only config value. Property attempted to set: " + key.key);
        }
    }

    // <editor-fold desc="Properties reading">
    /** Reads the packaged properties file for configuration. */
    private void readPackagedPropertiesFile() {
        LOGGER.trace("Reading properties file for properties.");
        try (InputStream is =
                DesktopAppConfiguration.class
                        .getClassLoader()
                        .getResourceAsStream(PROPERTY_FILE_LOC)) {
            if (is == null) {
                throw new FileNotFoundException(
                        "Input stream was null, assuming file is not present.");
            }
            properties.load(is);
        } catch (FileNotFoundException e) {
            LOGGER.error("Properties file not found. Cannot read properties in. Error: ", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.error("Error reading properties file. Cannot read properties in. Error: ", e);
            throw new RuntimeException(e);
        }
    }

    /** Processes default values. */
    private void placePackagedDefaults() {
        LOGGER.trace("Processing default configuration values.");
        for (ConfigKeys key : ConfigKeys.getKeysWithDefaultFor()) {
            properties.put(key.defaultFor.key, properties.get(key.key));
        }
    }

    /** Gets configuration from environment config. */
    private void addEnvironmentConfig() {
        LOGGER.trace("Processing configuration from Environment.");
        Map<String, String> envVars = System.getenv();

        for (ConfigKeys key : ConfigKeys.getKeysWithEnv()) {
            if (envVars.containsKey(key.envVar)) {
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
        LOGGER.trace("Reading user config file for properties.");
        Properties userFileProps = new Properties();
        try (InputStream is =
                DesktopAppConfiguration.class
                        .getClassLoader()
                        .getResourceAsStream(getProperty(ConfigKeys.CONFIG_FILE))) {
            if (is == null) {
                throw new FileNotFoundException(
                        "Input stream was null, assuming user's configuration file is not present.");
            }
            userFileProps.load(is);
        } catch (FileNotFoundException e) {
            LOGGER.warn(
                    "User's configuration file not found. Cannot read properties in from user's config file. Error: ",
                    e);
        } catch (IOException e) {
            LOGGER.error(
                    "Error reading user's configuration file. Cannot read user's configuration in. Assumed that it is present, just not readable. Error: ",
                    e);
            throw new RuntimeException(e);
        }

        for (Map.Entry<Object, Object> curProp : userFileProps.entrySet()) {
            this.putProperty(
                    ConfigKeys.getKeyOf((String) curProp.getKey()), (String) curProp.getValue());
        }
    }

    /**
     * Adds the command line ops to the PROPERTIES object.
     *
     * @param ops The parsed command line ops to add to the properties.
     */
    private void processCmdLineOps(CommandLineConfig ops) {
        Class<? extends CommandLineConfig> clazz = ops.getClass();

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
                LOGGER.error("Could not get value from CommandLineConfig. Error: ", e);
                throw new RuntimeException(e);
            }

            if (value != null) {
                this.putProperty(propertiesOption.configKey(), (String) value);
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

    /** Logs out the properties held. */
    public void logOutProperties() {
        LOGGER.info("Configuration properties:");

        // TODO:: sort these somehow? Issue with Properties holding <Object, Object>
        getAllProperties().stream()
                .forEach(
                        curProp ->
                                LOGGER.info("    {} : {}", curProp.getKey(), curProp.getValue()));
    }

    /**
     * Sets a property in the configuration. Ensures the config property is read only and resolves
     * placeholders.
     *
     * @param key The key of the property to set.
     * @param value The value to give the property
     * @return The old value that was held at the key.
     */
    public String putProperty(ConfigKeys key, String value) {
        assertKeyIsNotReadOnly(key);
        String oldVal = (String) properties.put(key.key, value);
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
