package com.gjs.taskTimekeeper.desktopApp.managerIO;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.gjs.taskTimekeeper.baseCode.TimeManager;
import com.gjs.taskTimekeeper.baseCode.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * TODO:: read/write with gip compression?
 * https://stackoverflow.com/questions/40931524/writing-to-json-gz-file-using-jackson-object-mapper
 */
public class LocalFile extends ManagerIO {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFile.class);

    @Override
    public TimeManager load() {
        LOGGER.trace("Reading in saved data from local file.");
        // TODO:: if bad json, don't just overwrite with empty file.
        TimeManager manager = null;
        try (InputStream is =
                new FileInputStream(Configuration.getProperty(ConfigKeys.SAVE_FILE, File.class))) {
            manager = ObjectMapperUtilities.getDefaultMapper().readValue(is, TimeManager.class);
        } catch (MismatchedInputException e) {
            LOGGER.debug("Empty save file. Starting with new manager.");
            System.out.println("New data initialized.");
            manager = new TimeManager();
            this.save(manager);
        } catch (IOException e) {
            LOGGER.error("FAILED to read in saved data: ", e);
            System.err.println("FAILED to read save data in. Error: " + e.getMessage());
            return null;
        }
        LOGGER.trace("Done reading in save data.");
        return manager;
    }

    @Override
    public void save(TimeManager manager) {
        LOGGER.trace("Writing out changed data.");
        try (OutputStream os =
                new FileOutputStream(Configuration.getProperty(ConfigKeys.SAVE_FILE, File.class))) {
            ObjectMapperUtilities.getDefaultMapper().writeValue(os, manager);
        } catch (IOException e) {
            LOGGER.error("FAILED to write changes to save file. Error: ", e);
            System.err.println(
                    "FAILED to write changes to save file. File might have become corrupt or failed to save changes.");
            System.err.println("\tError: " + e.getMessage());
        }
        LOGGER.trace("Done writing out changed data.");
    }

    /**
     * Ensures files in configuration exist and are accessible.
     *
     * <p>If a file is present, accessible, the config value is changed to a File
     *
     * <p>If the file cannot be made/ accessed, the config value is changed to null, If the file is
     * marked as required, stops the run.
     *
     * <p>Used in Main, but this is best place to put this I guess.
     *
     * <p>TODO:: figure out how to test. TODO:: reconcile that some 'files' might be url's
     */
    public static void ensureFilesExistWritable() {
        Collection<ConfigKeys> fileKeys = ConfigKeys.getKeysThatAreFiles();

        for (ConfigKeys key : fileKeys) {
            String fileLocStr = Configuration.getProperty(key, String.class);

            if (fileLocStr == null) {
                LOGGER.debug("File location for {} was null.", key);
                if (key.needsFile) {
                    LOGGER.error("No file location given for {}", key);
                    System.err.println("ERROR:: Must specify file for " + key.key);
                    System.exit(1);
                }
                continue;
            }

            fileLocStr = replaceFilePlaceholders(fileLocStr);

            File file = new File(fileLocStr);

            try {
                file.getParentFile().mkdirs();
                if (file.createNewFile()) {
                    LOGGER.info("File did not exist previously, but was created: \"{}\"", file);
                } else {
                    LOGGER.info("File already present: \"{}\"", file);
                }
            } catch (IOException e) {
                if (key.needsFile) {
                    LOGGER.error("Could not create required file \"{}\"", file, e);
                    System.err.println(
                            "ERROR:: Could not create required file \""
                                    + file.toString()
                                    + "\". Error: \""
                                    + e.getMessage()
                                    + "\" Exiting.");
                    System.exit(1);
                } else {
                    LOGGER.warn("Could not create file \"{}\"", file, e);
                    System.err.println(
                            "WARN::  Could not create file \""
                                    + file.toString()
                                    + "\". Error: \""
                                    + e.getMessage()
                                    + "\"");
                    Configuration.setFile(key, null);
                    continue;
                }
            }
            // double check file is good to go
            if (file.exists() && file.isFile() && file.canRead() && file.canWrite()) {
                LOGGER.debug("Verified file at \"{}\" (exists and is accessible).", file);
                Configuration.setFile(key, file);
                continue;
            }
            // handle if not good to go
            if (key.needsFile) {
                LOGGER.error(
                        "Could not verify required file \"{}\" exists and has read/write privileges. Exiting.",
                        file);
                System.err.println(
                        "ERROR:: Could not verify required file exists and has read/write privileges. Exiting.");
                System.exit(1);
            } else {
                LOGGER.warn(
                        "Could not verify required file \"{}\" exists and has read/write privileges. Exiting.",
                        file);
                System.err.println(
                        "WARN:: Could not verify file \""
                                + file.toString()
                                + "\" exists and has read/write privileges.");
                Configuration.setFile(key, null);
            }
        }
    }

    private static String replaceFilePlaceholders(String filepath) {
        String workingString = filepath;
        // TODO:: document this
        workingString = workingString.replaceAll("\\{HOME}", System.getProperty("user.home"));

        return workingString;
    }
}
