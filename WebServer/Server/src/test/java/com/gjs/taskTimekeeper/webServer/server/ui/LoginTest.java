package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebAssertions.submitFormAndAssertElementsInvalid;
import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebAssertions.submitFormAndAssertFormErrorMessage;
import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebHelpers.clearForm;
import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebHelpers.submitForm;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class LoginTest extends ServerWebUiTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginTest.class);
	
	public LoginTest(ServerInfoBean infoBean) {
		super(infoBean);
	}
	
	private void testLoginForm(By by, boolean nav) {
		WebElement loginForm = this.wrapper.getDriver().findElement(by);
		TestUser testUser = this.userUtils.setupTestUser(true);
		
		if(nav) {
			this.wrapper.openNavMenu();
		}
		
		WebElement usernameEmailInput = loginForm.findElement(By.name("usernameEmail"));
		WebElement passwordInput = loginForm.findElement(By.name("password"));
		WebElement rememberInput = loginForm.findElement(By.name("stayLoggedIn"));
		
		{
			submitFormAndAssertElementsInvalid(
				"name",
				(RemoteWebElement)loginForm,
				"usernameEmail",
				"password"
			);
			this.wrapper.assertLoggedOut();
			clearForm(loginForm);
			
			passwordInput.sendKeys(testUser.getPlainPassword());
			
			submitFormAndAssertElementsInvalid(
				"name",
				(RemoteWebElement)loginForm,
				"usernameEmail"
			);
			this.wrapper.assertLoggedOut();
			clearForm(loginForm);
			
			usernameEmailInput.sendKeys(testUser.getUsername());
			
			submitFormAndAssertElementsInvalid(
				"name",
				(RemoteWebElement)loginForm,
				"password"
			);
			this.wrapper.assertLoggedOut();
			clearForm(loginForm);
		}
		
		//bad login
		{
			usernameEmailInput.sendKeys(testUser.getUsername() + "baaaaddddd");
			passwordInput.sendKeys(testUser.getPlainPassword());
			
			submitFormAndAssertFormErrorMessage(
				this.wrapper,
				loginForm,
				"Error! No user with given username or email found.",
				"Not Found"
			);
			this.wrapper.assertLoggedOut();
			this.wrapper.closeAllMessages();
			clearForm(loginForm);
			
			
			usernameEmailInput.sendKeys(testUser.getUsername());
			passwordInput.sendKeys(testUser.getPlainPassword() + "oops");
			
			submitFormAndAssertFormErrorMessage(
				this.wrapper,
				loginForm,
				"Error! Password given was incorrect.",
				"Unauthorized"
			);
			this.wrapper.assertLoggedOut();
			this.wrapper.closeAllMessages();
			clearForm(loginForm);
		}
		
		usernameEmailInput.sendKeys(testUser.getEmail());
		passwordInput.sendKeys(testUser.getPlainPassword());
		submitForm(loginForm);
		
		this.wrapper.waitForPageRefreshingFormToComplete(true);
		
		assertEquals(
			testUser.getUsername(),
			this.wrapper.waitForElement(By.id("navUsername")).getText()
		);
		this.wrapper.assertLoggedIn(testUser);
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
		this.wrapper.navigateTo();
		
		testLoginForm(By.id("navbarLogin"), true);
	}
	
	@Test
	public void homeLogin() {
		this.wrapper.navigateTo();
		testLoginForm(By.id("homeLogin"), false);
	}
	
	@Test
	public void logout() {
		this.wrapper.navigateTo();
		this.wrapper.assertLoggedOut();
		
		TestUser testUser = this.userUtils.setupTestUser(true);
		
		this.wrapper.login(testUser);
		
		this.wrapper.openNavMenu();
		
		this.wrapper.getDriver().findElement(By.id("logoutButton")).click();
		
		this.wrapper.waitForPageRefreshingFormToComplete(false);
		
		this.wrapper.assertLoggedOut();
	}
}