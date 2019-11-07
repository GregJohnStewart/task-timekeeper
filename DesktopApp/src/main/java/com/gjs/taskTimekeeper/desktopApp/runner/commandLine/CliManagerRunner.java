package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import com.gjs.taskTimekeeper.baseCode.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSource;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import java.io.InputStream;
import java.util.Scanner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the management of TimeManager data in an interactive manner TODO:: use Outputter to be
 * more flexible
 */
public class CliManagerRunner extends ModeRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CliManagerRunner.class);

    private Scanner scanner = new Scanner(System.in);
    private ManagerIO managerIO;

    public CliManagerRunner(DesktopAppConfiguration config, Scanner scanner) {
        super(config);
        this.managerIO =
                new ManagerIO(DataSource.fromString(this.config.getProperty(ConfigKeys.SAVE_FILE)));
        this.scanner = scanner;
    }

    public CliManagerRunner(DesktopAppConfiguration config, InputStream is) {
        this(config, new Scanner(is));
    }

    public CliManagerRunner(DesktopAppConfiguration config) {
        this(config, System.in);
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
            System.out.print((managerIO.isUnSaved(false) ? "*" : "") + "> ");
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
                new CmdLineArgumentRunner(this.managerIO, this.config, false, input).run();
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
