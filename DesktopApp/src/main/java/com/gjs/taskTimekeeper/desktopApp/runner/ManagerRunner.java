package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.CmdLineArgumentRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.DoExit;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Scanner;

public class ManagerRunner extends ModeRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerRunner.class);

    private Scanner scanner = new Scanner(System.in);

    public ManagerRunner() {
        // no need to do anything
    }

    public ManagerRunner(Scanner scanner) {
        this.scanner = scanner;
    }

    public ManagerRunner(InputStream is) {
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
                new CmdLineArgumentRunner(false, input).run();
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
