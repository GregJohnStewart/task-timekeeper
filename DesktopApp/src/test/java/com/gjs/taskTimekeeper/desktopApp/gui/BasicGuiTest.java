package com.gjs.taskTimekeeper.desktopApp.gui;

import com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiNavigation;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.JInternalFrameFixture;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

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

        JInternalFrameFixture frame = this.fixture.internalFrame(new GenericTypeMatcher<JInternalFrame>(JInternalFrame.class) {
            @Override
            protected boolean isMatching(JInternalFrame component) {
                return true;
            }
        });

        frame.requireTitle("About Task Timekeeper");
    }
}
