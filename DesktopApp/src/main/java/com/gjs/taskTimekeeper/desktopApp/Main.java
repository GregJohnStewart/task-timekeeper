package com.gjs.taskTimekeeper.desktopApp;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.config.RunMode;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.CliManagerRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.CliSingleRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.GuiRunner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws CmdLineException {
        LOGGER.info("Starting run of TaskTimekeeper.");
        LOGGER.debug("Input arguments: {}", (Object[]) args);
        DesktopAppConfiguration properties = new DesktopAppConfiguration(args);
        LOGGER.info(
                "App version: {} Lib version: {}",
                properties.getProperty(ConfigKeys.APP_VERSION),
                properties.getProperty(ConfigKeys.LIB_VERSION));

        RunMode mode = null;
        try {
            mode = RunMode.valueOf(properties.getProperty(ConfigKeys.RUN_MODE).toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Bad run mode given. Error: ", e);
            // deal with later
        }

        if (mode != RunMode.SINGLE) {
            System.out.println(
                    "TaskTimekeeper v"
                            + properties.getProperty(ConfigKeys.APP_VERSION)
                            + " Using lib v"
                            + properties.getProperty(ConfigKeys.LIB_VERSION));
            System.out.println("\tGithub: " + properties.getProperty(ConfigKeys.GITHUB_REPO_URL));
            System.out.println();
        }
        if (mode == null) {
            System.err.println("Bad run mode given. Exiting.");
            return;
        }

        switch (mode) {
            case SINGLE:
                new CliSingleRunner(properties, args).run();
                break;
            case MANAGE:
                new CliManagerRunner(properties).run();
                break;
            case GUI_SWING:
                new GuiRunner(properties).run();
                break;
            default:
                LOGGER.error("Invalid run mode given.");
                throw new IllegalArgumentException("Invalid run mode given.");
        }
    }
}
