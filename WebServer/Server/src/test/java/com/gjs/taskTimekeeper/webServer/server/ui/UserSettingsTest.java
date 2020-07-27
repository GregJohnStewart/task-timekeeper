package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.service.TokenService;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebAssertions.submitFormAndAssertElementsInvalid;
import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.form.FormHelpers.resetForm;
import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.form.FormHelpers.submitForm;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
public class UserSettingsTest extends ServerWebUiTest {
	
	private final TokenService tokenService;
	private final PasswordService passwordService;
	
	public UserSettingsTest(
		ServerInfoBean infoBean,
		WebDriverWrapper wrapper,
		TokenService tokenService,
		PasswordService passwordService
	) {
		super(infoBean, wrapper);
		this.tokenService = tokenService;
		this.passwordService = passwordService;
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
			
			{
				submitFormAndAssertElementsInvalid(
					"name",
					(RemoteWebElement)passwordChangeForm,
					"currentPassword",
					"newPassword",
					"newPasswordConfirm"
				);
				
				resetForm(passwordChangeForm);
				
				currentPasswordInput.sendKeys(testUser.getPlainPassword());
				newPasswordInput.sendKeys(newPass);
				newPasswordConfirmInput.sendKeys(newPass + "bad");
				
				submitFormAndAssertElementsInvalid(
					"name",
					(RemoteWebElement)passwordChangeForm,
					"newPasswordConfirm"
				);
				
				resetForm(passwordChangeForm);
			}
			
			currentPasswordInput.sendKeys(testUser.getPlainPassword());
			newPasswordInput.sendKeys(newPass);
			newPasswordConfirmInput.sendKeys(newPass);
			
			submitForm(passwordChangeForm);
			
			this.wrapper.waitForElement(By.id("changePasswordSuccessMessage"));
			
			//TODO:: message check, email check
			this.passwordService.assertPasswordMatchesHash(testUser.getUserObj().getHashedPass(), newPass);
		}
	}
}
