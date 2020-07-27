package com.gjs.taskTimekeeper.desktopApp.gui.dataOperations.tasks;

import com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiNavigation;
import lombok.extern.slf4j.Slf4j;
import org.assertj.swing.fixture.JScrollPaneFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.gjs.taskTimekeeper.desktopApp.gui.utils.TestFileUtils.workingTestFile;

@Slf4j
public class ViewTaskTests extends TaskTest {
	
	@BeforeEach
	public void setup() throws Exception {
		this.startGui(workingTestFile);
		this.setupManagerIo(workingTestFile);
		GuiNavigation.selectTasksTab(this.fixture);
	}
	
	@Test
	public void testViewOfTasks() {
		JScrollPaneFixture tasksPaneFixture = this.fixture.scrollPane("tasksScrollPane");
		
		log.info("got tasks pane fixture.");
	}
}
