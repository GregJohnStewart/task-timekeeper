package com.gjs.taskTimekeeper.desktopApp;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.config.RunMode;
import com.gjs.taskTimekeeper.desktopApp.runner.CliManagerRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.CliSingleRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.GuiRunner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws CmdLineException {
        LOGGER.info("Starting run of TaskTimekeeper.");
        LOGGER.debug("Input arguments: {}", (Object[]) args);
        DesktopAppConfiguration.GLOBAL_CONFIG.finalizeConfig(args);
        LOGGER.info(
                "App version: {} Lib version: {}",
                DesktopAppConfiguration.GLOBAL_CONFIG.getProperty(
                        ConfigKeys.APP_VERSION, String.class),
                DesktopAppConfiguration.GLOBAL_CONFIG.getProperty(
                        ConfigKeys.LIB_VERSION, String.class));

        RunMode mode = null;
        try {
            mode =
                    RunMode.valueOf(
                            DesktopAppConfiguration.GLOBAL_CONFIG
                                    .getProperty(ConfigKeys.RUN_MODE, String.class)
                                    .toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Bad run mode given. Error: ", e);
            // deal with later
        }

        if (mode != RunMode.SINGLE) {
            System.out.println(
                    "TaskTimekeeper v"
                            + DesktopAppConfiguration.GLOBAL_CONFIG.getProperty(
                                    ConfigKeys.APP_VERSION, String.class)
                            + " Using lib v"
                            + DesktopAppConfiguration.GLOBAL_CONFIG.getProperty(
                                    ConfigKeys.LIB_VERSION, String.class));
            System.out.println(
                    "\tGithub: "
                            + DesktopAppConfiguration.GLOBAL_CONFIG.getProperty(
                                    ConfigKeys.GITHUB_REPO_URL, String.class));
            System.out.println();
        }
        if (mode == null) {
            System.err.println("Bad run mode given. Exiting.");
            return;
        }

        switch (mode) {
            case SINGLE:
                new CliSingleRunner(args).run();
                break;
            case MANAGE:
                new CliManagerRunner().run();
                break;
            case GUI_SWING:
                new GuiRunner().run();
                break;
            default:
                LOGGER.error("Invalid run mode given.");
                throw new IllegalArgumentException("Invalid run mode given.");
        }
    }
}
