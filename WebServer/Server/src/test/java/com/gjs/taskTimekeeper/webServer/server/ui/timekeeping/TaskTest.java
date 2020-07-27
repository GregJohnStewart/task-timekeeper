package com.gjs.taskTimekeeper.webServer.server.ui.timekeeping;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
public class TaskTest extends TimekeepingUiTest {
	public TaskTest(
		ServerInfoBean infoBean,
		WebDriverWrapper wrapper
	) {
		super(infoBean, wrapper);
	}
	
	@BeforeEach
	public void setup() {
		super.setup();
		
		this.wrapper.waitForElement(By.id("tasksTab")).click();
	}
	
	@Test
	public void addTaskTest() {
		WebElement addTaskButton = this.wrapper.waitForElement(By.id("addTaskButton"));
		
		addTaskButton.click();
	}
}
