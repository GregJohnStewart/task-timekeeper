package com.gjs.taskTimekeeper.desktopApp.gui.basic;

import com.gjs.taskTimekeeper.desktopApp.gui.GuiTest;
import com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiAssertions;
import com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiNavigation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gjs.taskTimekeeper.desktopApp.gui.utils.TestFileUtils.emptyTestFile;
import static com.gjs.taskTimekeeper.desktopApp.gui.utils.TestFileUtils.fullTestFile;

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
    public void testRefreshData() throws Exception {
        this.startGui(fullTestFile);

        GuiNavigation.clickMenuFile(this.fixture);
        GuiNavigation.clickMenuItem(this.fixture, "reloadDataMenuItem");
        GuiAssertions.assertNoDialog(this.fixture);
    }
}
