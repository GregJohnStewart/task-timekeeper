package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import com.gjs.taskTimekeeper.baseCode.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.FileDataSource;
import com.gjs.taskTimekeeper.baseCode.timeParser.TimeParser;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdLineArgumentRunner extends ModeRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdLineArgumentRunner.class);

    public static String[] splitCommandLineArgumentString(String inputString) {
        String[] inputs = inputString.split("(?<!\\\\)\\s+");

        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = inputs[i].replace("\\ ", " ");
        }

        return inputs;
    }

    private final CmdLineArgumentParser parser;
    private ManagerIO managerIO;

    public CmdLineArgumentRunner(DesktopAppConfiguration config, CmdLineArgumentParser parser) {
        super(config);
        this.parser = parser;
        // TODO:: make this more generic with the rewrite of configuration
        this.managerIO =
                new com.gjs.taskTimekeeper.baseCode.managerIO.ManagerIO(
                        new FileDataSource(this.config.getProperty(ConfigKeys.SAVE_FILE)));
    }

    public CmdLineArgumentRunner(DesktopAppConfiguration config, boolean allowExtra, String... args)
            throws CmdLineException {
        this(config, new CmdLineArgumentParser(allowExtra, args));
    }

    public CmdLineArgumentRunner(
            DesktopAppConfiguration config, boolean allowExtra, String inputString)
            throws CmdLineException {
        this(config, allowExtra, splitCommandLineArgumentString(inputString));
    }

    public CmdLineArgumentParser getParser() {
        return this.parser;
    }

    public ManagerIO getManagerIO() {
        return managerIO;
    }

    public CmdLineArgumentRunner setManagerIO(ManagerIO managerIO) {
        this.managerIO = managerIO;
        return this;
    }

    @Override
    public void run() {
        this.run(false);
    }

    public void run(boolean selectLatest) {
        ActionConfig actionConfig = this.parser.getConfig();
        LOGGER.trace("Running argument based on parsed argument: {}", actionConfig);

        if (actionConfig.getQuit()) {
            LOGGER.debug("User chose to exit.");
            throw new DoExit();
        }

        if (actionConfig.getShowHelp()) {
            LOGGER.debug("Showing help output.");
            showArgHelp();
            return;
        }

        if (selectLatest) {
            LOGGER.trace("Selecting the latest period.");
            this.managerIO.getManager().getCrudOperator().setNewestPeriodAsSelectedQuiet();
        }

        LOGGER.trace("FINISHED processing argument.");
    }

    /** Displays the help output to the terminal. */
    public void showArgHelp() {
        LOGGER.info("Showing argument help output to terminal.");

        System.out.println("Help output:");
        System.out.println(
                "\tFor more detailed information, visit: "
                        + this.config.getProperty(ConfigKeys.GITHUB_DESKTOP_APP_README));
        System.out.println();

        this.parser.printUsage();

        TimeParser.outputHelp();
    }
}
