package com.gjs.taskTimekeeper.desktopApp.gui;

import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicGuiTest extends GuiTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicGuiTest.class);

    @Test
    public void runBasicGuiPopulated() throws CmdLineException, InterruptedException {
        this.startGui(fullTestFile);
        LOGGER.info("Started with fully populated test file.");
    }

    @Test
    public void runBasicGuiEmpty() throws CmdLineException, InterruptedException {
        this.startGui(emptyTestFile);
        LOGGER.info("Started with empty test file.");
    }
}
