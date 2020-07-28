package com.gjs.taskTimekeeper.desktopApp.gui.basic;

import com.gjs.taskTimekeeper.desktopApp.gui.GuiTest;
import com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiAssertions;
import com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiNavigation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static com.gjs.taskTimekeeper.desktopApp.gui.utils.TestFileUtils.emptyTestFile;
import static com.gjs.taskTimekeeper.desktopApp.gui.utils.TestFileUtils.fullTestFile;

@Slf4j
public class BasicGuiTest extends GuiTest {
	
	@Test
	public void runBasicGuiPopulated() throws Exception {
		this.startGui(fullTestFile);
		log.info("Started with fully populated test file.");
	}
	
	@Test
	public void runBasicGuiEmpty() throws Exception {
		this.startGui(emptyTestFile);
		log.info("Started with empty test file.");
	}
	
	@Test
	public void testRefreshData() throws Exception {
		this.startGui(fullTestFile);
		
		GuiNavigation.clickMenuFile(this.fixture);
		GuiNavigation.clickMenuItem(this.fixture, "reloadDataMenuItem");
		GuiAssertions.assertNoDialog(this.fixture);
	}
}
