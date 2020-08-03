package com.gjs.taskTimekeeper.webServer.server.ui.timekeeping;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity.MANAGER_MAPPER;
import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingHelpers.getTimeDataLastDataChange;
import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingHelpers.getTimeDataLastDataLoad;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimekeepingAssertions {
	
	public static void assertUserTimeData(TestUser testUser, WebDriverWrapper wrapper, TimeManager expectedData)
		throws IOException {
		User user = testUser.getUserObj();
		ManagerEntity managerEntity = ManagerEntity.findByUserId(user.id);
		
		//TODO:: get from endpoint, with stats and sanitization on
		TimeManager held = MANAGER_MAPPER.readValue(managerEntity.getTimeManagerData(), TimeManager.class);
		
		assertEquals(expectedData, held);
		
		//TODO:: assert that data on page matches held manager data
		WebElement tasksTab = wrapper.waitForElement(By.id("tasksTab"));
		WebElement periodsTab = wrapper.waitForElement(By.id("periodsTab"));
		WebElement selectedPeriodTab = wrapper.waitForElement(By.id("selectedPeriodTab"));
		WebElement statsTab = wrapper.waitForElement(By.id("statsTab"));
		
		//tasks
		{
			tasksTab.click();
			
			for(Task task : held.getTasks()) {
				WebElement taskTable = wrapper.waitForElement(By.id("tasksTableContent"));
				List<WebElement> taskTableElements = taskTable.findElements(By.xpath("//*[contains(text(),'" + task.getName()
																												   .getName() + "')]"));
				
				assertEquals(1, taskTableElements.size());
				
				WebElement taskRow = taskTableElements.get(0).findElement(By.xpath("./.."));
			}
		}
		
		//periods
		{
			//TODO
		}
		
		//selected period
		{
			//TODO
		}
		
		//stats
		{
			//TODO
		}
	}
	
	public static void assertLastDataLoadUpdated(LocalDateTime previousLastDataLoad, WebDriverWrapper wrapper) {
		LocalDateTime lastDataLoad = getTimeDataLastDataLoad(wrapper);
		
		assertTrue(previousLastDataLoad.isBefore(lastDataLoad));
	}
	
	public static void assertLastDataChangeUpdated(LocalDateTime previousLastDataChange, WebDriverWrapper wrapper) {
		LocalDateTime lastDataLoad = getTimeDataLastDataChange(wrapper);
		
		assertTrue(previousLastDataChange.isBefore(lastDataLoad));
	}
	
	public static void assertLastDataLoadNotUpdated(LocalDateTime previousLastDataLoad, WebDriverWrapper wrapper) {
		LocalDateTime lastDataLoad = getTimeDataLastDataLoad(wrapper);
		
		assertTrue(previousLastDataLoad.isEqual(
			lastDataLoad
		));
	}
	
	public static void assertLastDataChangeNotUpdated(LocalDateTime previousLastDataChange, WebDriverWrapper wrapper) {
		LocalDateTime lastDataLoad = getTimeDataLastDataChange(wrapper);
		
		assertTrue(previousLastDataChange.isEqual(lastDataLoad));
	}
	
	public static void assertLastDataLoadAndChangeUpdated(
		LocalDateTime previousLastDataLoad,
		LocalDateTime previousLastDataChange,
		WebDriverWrapper wrapper
	) {
		assertLastDataLoadUpdated(previousLastDataLoad, wrapper);
		assertLastDataChangeUpdated(previousLastDataChange, wrapper);
	}
	
	public static void assertLastDataLoadAndChangeNotUpdated(
		LocalDateTime previousLastDataLoad,
		LocalDateTime previousLastDataChange,
		WebDriverWrapper wrapper
	) {
		assertLastDataLoadNotUpdated(previousLastDataLoad, wrapper);
		assertLastDataChangeNotUpdated(previousLastDataChange, wrapper);
	}
}
