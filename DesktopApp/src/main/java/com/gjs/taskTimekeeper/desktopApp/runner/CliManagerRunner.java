package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.baseCode.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.FileDataSource;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.CmdLineArgumentRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.DoExit;
import java.io.File;
import java.io.InputStream;
import java.util.Scanner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles the management of TimeManager data in an interactive manner TODO:: test */
public class CliManagerRunner extends ModeRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CliManagerRunner.class);

    private Scanner scanner = new Scanner(System.in);
    private ManagerIO managerIO;

    public CliManagerRunner() {
        // TODO:: make this more generic with the rewrite of configuration
        this.managerIO =
                new ManagerIO(
                        new FileDataSource(
                                Configuration.GLOBAL_CONFIG.getProperty(
                                        ConfigKeys.SAVE_FILE, File.class)));
    }

    public CliManagerRunner(Scanner scanner) {
        this.scanner = scanner;
    }

    public CliManagerRunner(InputStream is) {
        this.scanner = new Scanner(is);
    }

    @Override
    public void run() {
        LOGGER.info("Running the interactive command line manager mode.");

        System.out.println("Manager Mode");
        // TODO:: more detail (file locs, etc), move to Outputter for outputting

        String input;
        while (true) {
            LOGGER.trace("Start of interactive loop.");
            System.out.println();
            System.out.print("> ");
            try {
                input = scanner.nextLine();
            } catch (Exception e) {
                LOGGER.error("Error thrown while getting input: ", e);
                LOGGER.error("Exiting.");
                System.err.println("Error thrown when getting input: " + e.getMessage());
                System.err.println("Exiting due to error.");
                break;
            }
            LOGGER.debug("Got the following input: {}", input);

            try {
                new CmdLineArgumentRunner(false, input).setManagerIO(this.managerIO).run();
            } catch (DoExit e) {
                break;
            } catch (CmdLineException e) {
                LOGGER.warn("Error thrown while processing arguments: ", e);
                System.err.println(
                        "Invalid argument(s) given. Please try again. Use '-h' if you need help.");
            }
        }

        LOGGER.info("Exiting management mode.");
        System.out.println("Exiting.");
    }
}
