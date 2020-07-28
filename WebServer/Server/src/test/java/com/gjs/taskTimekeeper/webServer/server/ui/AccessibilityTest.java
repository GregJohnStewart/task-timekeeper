package com.gjs.taskTimekeeper.webServer.server.ui;


import com.deque.axe.AXE;
import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URL;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Accessibility tests using AXE.
 * <p>
 * Rules: https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md
 * Examples: https://github.com/dequelabs/axe-selenium-java/blob/master/src/test/java/com/deque/axe/ExampleTest.java
 * JS Source (placed in resources): https://github.com/dequelabs/axe-selenium-java/blob/master/src/test/java/com/deque/axe/ExampleTest.java
 */
@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
@Slf4j
public class AccessibilityTest extends ServerWebUiTest {
	private static final URL scriptUrl = AccessibilityTest.class.getResource("/axe.min.js");
	private static final String[] EXCLUDED_RULES = new String[]{
		//Remove periodically to check for contrast issues.
		// Disabled due to contrast error on disabled items
		// expected violations on: #selectedPeriodTab
		"color-contrast"
	};
	
	private static String getExcludedRules() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("rules: {");
		
		boolean first = true;
		
		for(String curExcludedRule : EXCLUDED_RULES) {
			if(first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append("'")
			  .append(curExcludedRule)
			  .append("': { enabled: false }");
		}
		sb.append("}");
		String output = sb.toString();
		log.info("Rules to exclude: {}", output);
		return output;
	}
	
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
		JSONObject responseJSON = new AXE.Builder(this.wrapper.getDriver(), scriptUrl)
			.options("{ " + getExcludedRules() + " }")
			.analyze();
		
		JSONArray violations = responseJSON.getJSONArray("violations");
		
		assertEquals(0, violations.length(), AXE.report(violations));
	}
}
