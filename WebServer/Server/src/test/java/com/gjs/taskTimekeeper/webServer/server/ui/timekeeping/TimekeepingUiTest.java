package com.gjs.taskTimekeeper.webServer.server.ui.timekeeping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;

import java.time.LocalDateTime;

import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingAssertions.assertLastDataLoadAndChangeUpdated;
import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingHelpers.getTimeDataLastDataChange;
import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingHelpers.getTimeDataLastDataLoad;

public class TimekeepingUiTest extends ServerWebUiTest {
	protected TestUser testUser;
	protected LocalDateTime lastDataLoad;
	protected LocalDateTime lastDataChange;
	
	
	protected TimekeepingUiTest(
		ServerInfoBean infoBean,
		WebDriverWrapper wrapper
	) {
		super(infoBean, wrapper);
	}
	
	protected void setup() throws InterruptedException {
		this.testUser = this.userUtils.setupTestUser(true);
		this.wrapper.login(this.testUser);
		resetDataLoadChange();
	}
	
	protected void setupUserData(TimeManager timeManager) throws JsonProcessingException, InterruptedException {
		ManagerEntity entity = ManagerEntity.findByUserId(this.testUser.getUserObj().id);
		entity.updateManagerData(timeManager);
		entity.update();
		
		this.wrapper.reloadPage();
		this.wrapper.waitForPageLoad();
		this.resetDataLoadChange();
	}
	
	protected void resetDataLoadChange() throws InterruptedException {
		this.lastDataLoad = getTimeDataLastDataLoad(this.wrapper);
		this.lastDataChange = getTimeDataLastDataChange(this.wrapper);
		//sleep one second to guarantee changed values later
		Thread.sleep(1000);
	}
	
	protected void assertTimestampsUpdated() throws InterruptedException {
		assertLastDataLoadAndChangeUpdated(
			this.lastDataLoad,
			this.lastDataChange,
			this.wrapper
		);
		resetDataLoadChange();
	}
}
