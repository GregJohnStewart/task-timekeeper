package com.gjs.taskTimekeeper.webServer.server.ui.timekeeping;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;

public class TimekeepingUiTest extends ServerWebUiTest {
	protected TestUser testUser;
	
	protected TimekeepingUiTest(
		ServerInfoBean infoBean,
		WebDriverWrapper wrapper
	) {
		super(infoBean, wrapper);
	}
	
	protected void setup() {
		this.testUser = this.userUtils.setupTestUser(true);
		this.wrapper.login(this.testUser);
	}
}
