package com.gjs.taskTimekeeper.webServer.server.ui.timekeeping;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;


@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
public class TaskTest extends TimekeepingUiTest {
	public TaskTest(
		ServerInfoBean infoBean,
		WebDriverWrapper wrapper
	) {
		super(infoBean, wrapper);
	}
}
