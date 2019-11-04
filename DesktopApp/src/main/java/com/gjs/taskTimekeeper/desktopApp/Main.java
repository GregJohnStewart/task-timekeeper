package com.gjs.taskTimekeeper.desktopApp;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.config.RunMode;
import com.gjs.taskTimekeeper.desktopApp.managerIO.LocalFile;
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
        Configuration.finalizeConfig(args);
        LOGGER.info(
                "App version: {} Lib version: {}",
                Configuration.getProperty(ConfigKeys.APP_VERSION, String.class),
                Configuration.getProperty(ConfigKeys.LIB_VERSION, String.class));

        RunMode mode = null;
        try {
            mode =
                    RunMode.valueOf(
                            Configuration.getProperty(ConfigKeys.RUN_MODE, String.class)
                                    .toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Bad run mode given. Error: ", e);
            // deal with later
        }

        if (mode != RunMode.SINGLE) {
            System.out.println(
                    "TaskTimekeeper v"
                            + Configuration.getProperty(ConfigKeys.APP_VERSION, String.class)
                            + " Using lib v"
                            + Configuration.getProperty(ConfigKeys.LIB_VERSION, String.class));
            System.out.println(
                    "\tGithub: "
                            + Configuration.getProperty(ConfigKeys.GITHUB_REPO_URL, String.class));
            System.out.println();
        }
        if (mode == null) {
            System.err.println("Bad run mode given. Exiting.");
            return;
        }

        LocalFile.ensureFilesExistWritable();

        switch (mode) {
            case SINGLE:
                new CliSingleRunner(args).run();
                break;
            case MANAGE:
                new CliManagerRunner().run();
                break;
            case GUI:
                new GuiRunner().run();
                break;
            default:
                LOGGER.error("Invalid run mode given.");
                throw new IllegalArgumentException("Invalid run mode given.");
        }
    }
}
