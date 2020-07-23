package com.gjs.taskTimekeeper.webServer.server.ui;


import com.deque.axe.AXE;
import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
public class AccessibilityTest extends ServerWebUiTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(GeneralUiTest.class);
	private static final URL scriptUrl = AccessibilityTest.class.getResource("/axe.min.js");
	
	public AccessibilityTest(ServerInfoBean infoBean, WebDriverWrapper wrapper) {
		super(infoBean, wrapper);
	}
	
	private static Stream<Arguments> args() {
		return Stream.of(
			Arguments.of("", false),
			Arguments.of("", true),
			Arguments.of("/userSettings", true)
		);
	}
	
	@ParameterizedTest(name = "[{index}] {0} loggedIn: {1}")
	@MethodSource("args")
	public void wcagTest(String page, boolean loggedIn) {
		if(loggedIn) {
			TestUser testUser = this.userUtils.setupTestUser(true);
			this.wrapper.login(testUser);
		}
		this.wrapper.navigateTo(page);
		
		AXE.inject(this.wrapper.getDriver(), scriptUrl);
		JSONObject responseJSON = new AXE.Builder(this.wrapper.getDriver(), scriptUrl).analyze();
		
		JSONArray violations = responseJSON.getJSONArray("violations");
		
		assertEquals(0, violations.length(), AXE.report(violations));
	}
}
