package com.gjs.taskTimekeeper.desktopApp.gui;

import com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiNavigation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicGuiTest extends GuiTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicGuiTest.class);

    @Test
    public void runBasicGuiPopulated() throws Exception {
        this.startGui(fullTestFile);
        LOGGER.info("Started with fully populated test file.");
    }

    @Test
    public void runBasicGuiEmpty() throws Exception {
        this.startGui(emptyTestFile);
        LOGGER.info("Started with empty test file.");
    }

    @Test
    public void infoMenuBar() throws Exception {
        this.startGui(emptyTestFile);
        GuiNavigation.clickMenuInfo(this.fixture);
        GuiNavigation.clickMenuItem(this.fixture, "aboutMenuItem");

        //TODO:: finish. can't do internal dialog?
//        this.fixture.dialog(DialogMatcher.withTitle("About Task Timekeeper").andShowing());
    }
}
