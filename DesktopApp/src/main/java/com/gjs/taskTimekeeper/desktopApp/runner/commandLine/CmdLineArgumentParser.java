package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parser of command line arguments. Handles parsing overall configuration and just what is needed
 * to run
 */
public class CmdLineArgumentParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdLineArgumentParser.class);

    private final ActionConfig config = new ActionConfig();

    private final String[] argsGotten;
    // receives other command line parameters than options
    @Argument private List<String> arguments = new ArrayList<>();
    private final CmdLineParser parser = new CmdLineParser(this.config);

    public CmdLineArgumentParser(boolean allowExtra, String[] args) throws CmdLineException {
        LOGGER.trace("Parsing command line ops.");
        LOGGER.debug("Command line ops given ({}): {}", args.length, args);
        this.argsGotten = Arrays.copyOf(args, args.length);

        try {
            this.parser.parseArgument(this.argsGotten);
        } catch (CmdLineException e) {
            // ignore invalid options.
            if (!allowExtra && !e.getMessage().contains("is not a valid option")) {
                throw e;
            }
        }
    }

    public ActionConfig getConfig() {
        return this.config;
    }

    public void printUsage() {
        this.parser.printUsage(System.out);
    }

    public String[] getArgsGotten() {
        return this.argsGotten;
    }
}
