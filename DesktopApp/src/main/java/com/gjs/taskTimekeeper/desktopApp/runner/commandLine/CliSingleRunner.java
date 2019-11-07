package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Runner for a single cli input. TODO:: test */
public class CliSingleRunner extends ModeRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CliSingleRunner.class);

    private final String[] args;

    public CliSingleRunner(DesktopAppConfiguration config, String[] args) {
        super(config);
        this.args = args;
    }

    public void run() {
        LOGGER.info("Running in single command mode.");
        // System.out.println("Running single command.");

        try {
            CmdLineArgumentRunner runner = new CmdLineArgumentRunner(this.config, true, this.args);
            runner.getManagerIO().setAutoSave(true);
            runner.run(true);
        } catch (CmdLineException e) {
            LOGGER.error("Exception when running command: ", e);
            System.err.println("Error when running command: " + e.getMessage());
        }
    }
}
