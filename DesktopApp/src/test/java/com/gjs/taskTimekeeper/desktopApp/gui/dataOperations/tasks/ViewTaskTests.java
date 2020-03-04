package com.gjs.taskTimekeeper.desktopApp.gui.dataOperations.tasks;

import com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiNavigation;
import org.junit.Before;
import org.junit.Test;

import static com.gjs.taskTimekeeper.desktopApp.gui.utils.TestFileUtils.workingTestFile;

public class ViewTaskTests extends TaskTest {
    @Before
    public void setup() throws Exception {
        this.startGui(workingTestFile);
        this.setupManagerIo(workingTestFile);
        GuiNavigation.selectTasksTab(this.fixture);
    }

    @Test
    public void testViewOfTasks(){
//        ManagerIO
    }
}
