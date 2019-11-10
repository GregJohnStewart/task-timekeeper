package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

public class CmdLineArgumentParserTest {

    @Test(expected = CmdLineException.class)
    public void badConfigGiven() throws CmdLineException {
        new CmdLineArgumentParser(false, new String[] {""});
    }

    @Test
    public void getConfig() throws CmdLineException {
        CmdLineArgumentParser parser = new CmdLineArgumentParser(false, new String[0]);
        assertNotNull(parser.getConfig());
    }

    @Test
    public void printUsage() throws CmdLineException {
        new CmdLineArgumentParser(false, new String[0]).printUsage();
    }

    @Test
    public void getArgsGotten() throws CmdLineException {
        String[] args = new String[0];
        CmdLineArgumentParser parser = new CmdLineArgumentParser(false, args);
        assertArrayEquals(args, parser.getArgsGotten());
    }
}
