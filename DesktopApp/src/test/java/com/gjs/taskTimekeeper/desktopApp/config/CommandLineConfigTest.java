package com.gjs.taskTimekeeper.desktopApp.config;

import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommandLineConfigTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineConfigTest.class);

    @Test
    public void test() throws CmdLineException {
        CommandLineConfig cfg = new CommandLineConfig("-h", "--configFile", "textConfig.txt");

        assertEquals("textConfig.txt", cfg.getConfigLoc());

        cfg = new CommandLineConfig("-p", "-h");

        assertNotNull(cfg.getParser());
    }
}
