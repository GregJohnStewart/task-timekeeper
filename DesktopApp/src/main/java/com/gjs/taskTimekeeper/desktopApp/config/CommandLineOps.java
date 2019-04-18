package com.gjs.taskTimekeeper.desktopApp.config;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandLineOps {
    private final Logger LOGGER = LoggerFactory.getLogger(CommandLineOps.class);

    private final String[] argsGotten;

    @Option(name = "-m", aliases = {"--mode"}, usage = "The mode that this will run with. Default SINGLE.")
    private RunMode runMode = null;

    @Option(name = "-h", aliases = {"--help"}, usage = "Show this help dialogue.")
    private boolean showHelp = false;


    // receives other command line parameters than options
    @Argument
    private List<String> arguments = new ArrayList<>();

    public CommandLineOps(String[] args) {
        this.argsGotten = Arrays.copyOf(args, args.length);

        CmdLineParser parser = new CmdLineParser(this);

        try {
            // parse the arguments.
            parser.parseArgument(this.argsGotten);

            //if (this.argsGotten.length == 0) {
            //	throw new IllegalArgumentException("No arguments given");
            //}
            if (this.showHelp) {
                System.out.println("Available options:");
                parser.printUsage(System.out);
                System.exit(0);
            }
        } catch (CmdLineException | IllegalArgumentException e) {
            System.err.println("Error parsing arguments:");
            System.err.println("\t" + e.getMessage());
            System.err.println("");
            // print the list of available options
            System.err.println("Available options:");
            parser.printUsage(System.err);
            System.err.println();
            System.exit(1);
        }

    }
}
