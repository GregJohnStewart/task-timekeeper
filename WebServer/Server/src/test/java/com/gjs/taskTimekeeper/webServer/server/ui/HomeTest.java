package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import io.quarkus.mailer.Mail;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.List;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebAssertions.submitFormAndAssertElementsInvalid;
import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.form.FormHelpers.resetForm;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
@Slf4j
class HomeTest extends ServerWebUiTest {
	
	HomeTest(ServerInfoBean infoBean, WebDriverWrapper wrapper) {
		super(infoBean, wrapper);
	}
	
	@Test
	public void basicLoadTest() {
		log.info("Loading the home page.");
		this.wrapper.navigateTo("");
		
		log.info("Loaded the home page.");
	}
	
	@Test
	public void testUserAccountCreationForm(){
		this.wrapper.navigateTo("");
		this.userUtils.setupTestUser(false);
		
		WebElement createAccountForm = this.wrapper.getDriver().findElement(By.id("homeCreateAccount"));
		
		WebElement createAccountEmailInput = createAccountForm.findElement(By.id("createAccountEmail"));
		WebElement createAccountUsernameInput = createAccountForm.findElement(By.id("createAccountUsername"));
		WebElement createAccountPasswordInput = createAccountForm.findElement(By.id("createAccountPassword"));
		WebElement createAccountPasswordConfirmInput = createAccountForm.findElement(
			By.id("createAccountPasswordConfirm")
		);
		
		
		WebElement createAccountSubmitButton = createAccountForm.findElement(By.id("createAccountSubmitButton"));
		
		//doesn't submit on no input
		{
			submitFormAndAssertElementsInvalid(
				"id",
				(RemoteWebElement)createAccountForm,
				"createAccountEmail",
				"createAccountUsername",
				"createAccountPassword",
				"createAccountPasswordConfirm"
			);
		}
		resetForm(createAccountForm);
		
		TestUser testUser = this.userUtils.setupTestUser();
		//doesn't submit with fine input, except for bad password confirm
		{
			
			createAccountEmailInput.sendKeys(testUser.getEmail());
			createAccountUsernameInput.sendKeys(testUser.getUsername());
			createAccountPasswordInput.sendKeys(testUser.getPlainPassword());
			createAccountPasswordConfirmInput.sendKeys(testUser.getPlainPassword() + "hello world");
			
			submitFormAndAssertElementsInvalid(
				"id",
				(RemoteWebElement)createAccountForm,
				"createAccountPasswordConfirm"
			);
		}
		
		//submits, have new user, got email
		{
			createAccountPasswordConfirmInput.clear();
			createAccountPasswordConfirmInput.sendKeys(testUser.getPlainPassword());
			
			createAccountSubmitButton.click();
			
			this.wrapper.waitForElement(By.id("createAccountSuccessMessage"));
			
			User user = User.findByEmail(testUser.getEmail());
			
			List<Mail> sent = mailbox.getMessagesSentTo(user.getEmail());
			assertEquals(1, sent.size());
			Mail actual = sent.get(0);
			
			assertEquals(
				"Welcome to the TaskTimekeeper Server",
				actual.getSubject()
			);
		}
		
		log.info("Done.");
	}
}