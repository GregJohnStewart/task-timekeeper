package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebAssertions.assertPageHasMessage;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class LoginRequiredUiTest extends ServerWebUiTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginRequiredUiTest.class);
	
	public LoginRequiredUiTest(ServerInfoBean infoBean) {
		super(infoBean);
	}
	
	public static Stream<Arguments> getPagesThatRequireLogin() {
		return Stream.of(
			//fails null
			Arguments.of(
				"/userSettings"
			)
		);
	}
	
	@ParameterizedTest
	@MethodSource("getPagesThatRequireLogin")
	public void testPageKicksToHome(String page) {
		this.wrapper.navigateTo(page);
		this.wrapper.waitForPageRefreshingFormToComplete(false);
		assertPageHasMessage(
			this.wrapper,
			"danger",
			null,
			"Must be logged in to access that page."
		);
	}
}