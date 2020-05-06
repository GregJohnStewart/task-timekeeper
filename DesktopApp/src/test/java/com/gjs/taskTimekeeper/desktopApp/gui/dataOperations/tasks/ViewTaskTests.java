package com.gjs.taskTimekeeper.desktopApp.gui.dataOperations.tasks;

import com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiNavigation;
import org.assertj.swing.fixture.JScrollPaneFixture;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gjs.taskTimekeeper.desktopApp.gui.utils.TestFileUtils.workingTestFile;

public class ViewTaskTests extends TaskTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewTaskTests.class);

    @Before
    public void setup() throws Exception {
        this.startGui(workingTestFile);
        this.setupManagerIo(workingTestFile);
        GuiNavigation.selectTasksTab(this.fixture);
    }

    @Test
    public void testViewOfTasks(){
        JScrollPaneFixture tasksPaneFixture = this.fixture.scrollPane("tasksScrollPane");

        LOGGER.info("got tasks pane fixture.");


    }
}
