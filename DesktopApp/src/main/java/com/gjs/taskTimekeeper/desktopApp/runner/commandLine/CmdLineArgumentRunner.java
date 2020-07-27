package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import com.gjs.taskTimekeeper.baseCode.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSource;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceParsingException;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.utils.CommandLineArgumentSplitter;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;

@Slf4j
public class CmdLineArgumentRunner extends ModeRunner {
    private final CmdLineArgumentParser parser;
    private ManagerIO managerIO;
    
    public CmdLineArgumentRunner(
        ManagerIO managerIO, DesktopAppConfiguration config, CmdLineArgumentParser parser)
        throws DataSourceParsingException {
        super(config);
        this.parser = parser;
        this.managerIO = managerIO;
    }
    
    public CmdLineArgumentRunner(
        ManagerIO managerIO, DesktopAppConfiguration config, boolean allowExtra, String... args)
        throws CmdLineException {
        this(managerIO, config, new CmdLineArgumentParser(allowExtra, args));
    }
    
    public CmdLineArgumentRunner(
        ManagerIO managerIO,
        DesktopAppConfiguration config,
        boolean allowExtra,
        String inputString)
        throws CmdLineException {
        this(managerIO, config, allowExtra, CommandLineArgumentSplitter.split(inputString));
    }
    
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
        log.trace("Running argument based on parsed argument: {}", actionConfig);
        
        if (actionConfig.getQuit()) {
            log.debug("User chose to exit.");
            throw new DoExit();
        }
        
        if (actionConfig.getShowHelp()) {
            log.debug("Showing help output.");
            showArgHelp();
            return;
        }
        
        if (actionConfig.getSave()) {
            log.debug("Saving the time manager.");
            this.managerIO.saveManager();
            return;
        }
        
        if (selectLatest) {
            log.debug("Selecting the latest period.");
            this.managerIO.getManager().getCrudOperator().setNewestPeriodAsSelectedQuiet();
        }
        
        this.managerIO.doCrudAction(actionConfig);
        
        log.trace("FINISHED processing argument.");
    }
    
    /** Displays the help output to the terminal. */
    public void showArgHelp() {
        log.info("Showing argument help output to terminal.");
        
        System.out.println("Help output:");
        System.out.println(
            "\tFor more detailed information, visit: "
                + this.config.getProperty(ConfigKeys.GITHUB_DESKTOP_APP_README));
        System.out.println();
        
        this.parser.printUsage();
        
        TimeParser.outputHelp();
    }
}
