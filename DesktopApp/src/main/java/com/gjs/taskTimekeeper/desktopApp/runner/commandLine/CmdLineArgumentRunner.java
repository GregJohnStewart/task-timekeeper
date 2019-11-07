package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import com.gjs.taskTimekeeper.baseCode.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSource;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceParsingException;
import com.gjs.taskTimekeeper.baseCode.timeParser.TimeParser;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.utils.CommandLineArgumentSplitter;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdLineArgumentRunner extends ModeRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdLineArgumentRunner.class);

    private final CmdLineArgumentParser parser;
    private ManagerIO managerIO;

    public CmdLineArgumentRunner(DesktopAppConfiguration config, CmdLineArgumentParser parser)
            throws DataSourceParsingException {
        super(config);
        this.parser = parser;
        this.managerIO =
                new ManagerIO(DataSource.fromString(this.config.getProperty(ConfigKeys.SAVE_FILE)));
    }

    public CmdLineArgumentRunner(DesktopAppConfiguration config, boolean allowExtra, String... args)
            throws CmdLineException {
        this(config, new CmdLineArgumentParser(allowExtra, args));
    }

    public CmdLineArgumentRunner(
            DesktopAppConfiguration config, boolean allowExtra, String inputString)
            throws CmdLineException {
        this(config, allowExtra, CommandLineArgumentSplitter.split(inputString));
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

        if (actionConfig.isSave()) {
            LOGGER.debug("Saving the time manager.");
            this.managerIO.saveManager();
            return;
        }

        if (selectLatest) {
            LOGGER.debug("Selecting the latest period.");
            this.managerIO.getManager().getCrudOperator().setNewestPeriodAsSelectedQuiet();
        }

        this.managerIO.doCrudAction(actionConfig);

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
