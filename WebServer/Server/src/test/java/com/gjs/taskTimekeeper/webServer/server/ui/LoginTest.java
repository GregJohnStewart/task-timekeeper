package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class LoginTest extends ServerWebUiTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginTest.class);
	
	public LoginTest(ServerInfoBean infoBean) {
		super(infoBean);
	}
	
	private void testLoginForm(By by) {
		//TODO
	}
	
	@Test
	public void notLoggedInUiElements() {
		LOGGER.info("Loading the home page.");
		this.wrapper.navigateTo("");
		LOGGER.info("Loaded the home page.");
		
		this.wrapper.assertLoggedOut();
		
		WebDriverWait pageLoadWait = new WebDriverWait(this.wrapper.getDriver(), 10);
		
		pageLoadWait.until(
			driver->driver.findElement(By.id("navbarLoginContent"))
		);
		
		this.wrapper.getDriver().findElement(By.id("navbarLoginContent")).isDisplayed();
	}
	
	@Test
	public void navbarLogin() {
		LOGGER.info("Loading the home page.");
		this.wrapper.navigateTo();
		LOGGER.info("Loaded the home page.");
		
		TestUser testUser = this.userUtils.setupTestUser(true);
		
		
		//TODO:: invalid data tests
		
		this.wrapper.getDriver().findElement(By.id("loginNavText")).click();
		
		this.wrapper.getDriver()
					.findElement(By.id("navbarLoginEmailUsername"))
					.sendKeys(testUser.getEmail());
		this.wrapper.getDriver()
					.findElement(By.id("navbarLoginPassword"))
					.sendKeys(testUser.getPlainPassword());
		this.wrapper.getDriver().findElement(By.id("navbarLoginSubmitButton")).click();
		
		this.wrapper.waitForPageRefreshingFormToComplete(true);
		
		assertEquals(
			testUser.getUsername(),
			this.wrapper.waitForElement(By.id("navUsername")).getText()
		);
	}
}
