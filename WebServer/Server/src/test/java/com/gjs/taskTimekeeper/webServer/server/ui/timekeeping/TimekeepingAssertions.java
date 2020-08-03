package com.gjs.taskTimekeeper.webServer.server.ui.timekeeping;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.rest.TestRestUtils;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.TimeManagerResponse;
import io.restassured.response.ValidatableResponse;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingHelpers.getTimeDataLastDataChange;
import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingHelpers.getTimeDataLastDataLoad;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimekeepingAssertions {
	
	private static TimeManagerResponse getHeldTimeManagerData(String jwt) {
		ValidatableResponse validatableResponse = TestRestUtils
			.newJwtCall(jwt)
			.header("provideStats", true)
			.header("sanitizeText", true)
			.get("/api/timeManager/manager")
			.then();
		
		return validatableResponse.extract().body().as(TimeManagerResponse.class);
	}
	
	public static void assertUserTimeData(
		TestUser testUser,
		WebDriverWrapper wrapper,
		TimeManager expectedData,
		String userJwt
	)
		throws IOException {
		User user = testUser.getUserObj();
		ManagerEntity managerEntity = ManagerEntity.findByUserId(user.id);
		
		TimeManagerResponse getResponse = getHeldTimeManagerData(userJwt);
		TimeManager held = getResponse.getTimeManagerData();
		
		assertEquals(expectedData, held);
		
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
