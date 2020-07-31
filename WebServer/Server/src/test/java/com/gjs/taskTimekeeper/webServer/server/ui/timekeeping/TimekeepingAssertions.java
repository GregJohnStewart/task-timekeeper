package com.gjs.taskTimekeeper.webServer.server.ui.timekeeping;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity.MANAGER_MAPPER;
import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingHelpers.getTimeDataLastDataChange;
import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingHelpers.getTimeDataLastDataLoad;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimekeepingAssertions {
	
	public static void assertUserTimeData(TestUser testUser, TimeManager expectedData) throws IOException {
		User user = testUser.getUserObj();
		ManagerEntity managerEntity = ManagerEntity.findByUserId(user.id);
		
		TimeManager held = MANAGER_MAPPER.readValue(managerEntity.getTimeManagerData(), TimeManager.class);
		
		assertEquals(expectedData, held);
		
		//TODO:: assert that data on page matches held manager data
		
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
