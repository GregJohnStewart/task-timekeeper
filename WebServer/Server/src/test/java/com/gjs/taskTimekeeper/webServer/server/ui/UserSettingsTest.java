package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.service.TokenService;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebHelpers.submitForm;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class UserSettingsTest extends ServerWebUiTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingsTest.class);
	
	private final TokenService tokenService;
	
	public UserSettingsTest(
		ServerInfoBean infoBean,
		TokenService tokenService
	) {
		super(infoBean);
		this.tokenService = tokenService;
	}
	
	@Test
	public void testLoginSettings() {
		TestUser testUser = this.userUtils.setupTestUser(true);
		this.wrapper.login(testUser);
		this.wrapper.navigateTo("/userSettings");
		
		this.wrapper.getDriver().findElement(By.id("loginSettingsTab")).click();
		
		// password change
		{
			this.wrapper.getDriver().findElement(By.id("passwordChangeHeader")).click();
			
			WebElement passwordChangeForm = this.wrapper.getDriver().findElement(By.id("changePasswordForm"));
			WebElement currentPasswordInput = passwordChangeForm.findElement(By.id("changePasswordCurrentPassword"));
			WebElement newPasswordInput = passwordChangeForm.findElement(By.id("changePasswordNewPassword"));
			WebElement newPasswordConfirmInput = passwordChangeForm.findElement(By.id("changePasswordNewPasswordConfirm"));
			
			String newPass = tokenService.generateToken();
			
			//TODO:: error states
			
			currentPasswordInput.sendKeys(testUser.getPlainPassword());
			newPasswordInput.sendKeys(newPass);
			newPasswordConfirmInput.sendKeys(newPass);
			
			submitForm(passwordChangeForm);
			
			this.wrapper.waitForElement(By.id("changePasswordSuccessMessage"));
			
			//TODO:: message check, email check, updated password check
			
		}
	}
}
