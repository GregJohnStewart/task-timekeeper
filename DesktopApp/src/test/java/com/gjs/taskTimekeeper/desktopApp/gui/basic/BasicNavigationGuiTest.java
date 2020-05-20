package com.gjs.taskTimekeeper.desktopApp.gui.basic;

import com.gjs.taskTimekeeper.desktopApp.gui.GuiTest;
import com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiNavigation;
import com.gjs.taskTimekeeper.desktopApp.gui.utils.TestFileUtils;
import com.gjs.taskTimekeeper.desktopApp.gui.utils.matcher.ButtonTextMatcher;
import com.gjs.taskTimekeeper.desktopApp.gui.utils.matcher.InternalFrameMatcher;
import org.assertj.swing.data.Index;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JInternalFrameFixture;
import org.assertj.swing.fixture.JTabbedPaneFixture;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gjs.taskTimekeeper.desktopApp.gui.utils.TestFileUtils.fullTestFile;

public class BasicNavigationGuiTest extends GuiTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicNavigationGuiTest.class);

    @Test
    public void infoMenuBar() throws Exception {
        this.startGui(TestFileUtils.emptyTestFile);
        GuiNavigation.clickMenuInfo(this.fixture);
        GuiNavigation.clickMenuItem(this.fixture, "aboutMenuItem");

        JInternalFrameFixture frame = this.fixture.internalFrame(new InternalFrameMatcher());

        frame.requireTitle("About Task Timekeeper");

        JButtonFixture jButtonFixture = frame.button(new ButtonTextMatcher("OK"));

        LOGGER.info("Ensuring visible.");
        frame.requireVisible();

        LOGGER.info("Clicking ok button.");
        jButtonFixture.click();

        LOGGER.info("Requiring frame closed.");

        frame.requireNotVisible();
    }

    @Test
    public void mainTabEmpty() throws Exception {
        this.startGui(TestFileUtils.emptyTestFile);

        JTabbedPaneFixture mainTabFixture = this.fixture.tabbedPane("mainTabPane");

        //ensure selected period tab is disabled
        mainTabFixture.requireDisabled(Index.atIndex(0));

        //ensure starting on "Periods" tab
        mainTabFixture.requireSelectedTab(Index.atIndex(1));

        //unsure if really necessary
        GuiNavigation.selectTasksTab(this.fixture);
        GuiNavigation.selectStatsTab(this.fixture);
        GuiNavigation.selectPeriodsTab(this.fixture);
    }

    @Test
    public void mainTabPopulated() throws Exception {
        this.startGui(fullTestFile);

        JTabbedPaneFixture mainTabFixture = this.fixture.tabbedPane("mainTabPane");

        //ensure selected period tab is enabled
        mainTabFixture.requireEnabled(Index.atIndex(0));

        //ensure starting on "Selected Periods" tab
        mainTabFixture.requireSelectedTab(Index.atIndex(0));

        //unsure if really necessary
        GuiNavigation.selectPeriodsTab(this.fixture);
        GuiNavigation.selectTasksTab(this.fixture);
        GuiNavigation.selectStatsTab(this.fixture);
        GuiNavigation.selectSelectedPeriodTab(this.fixture);
    }
}
