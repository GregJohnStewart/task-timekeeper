package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import io.quarkus.mailer.Mail;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebAssertions.submitFormAndAssertElementsInvalid;
import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebHelpers.clearForm;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
class HomeTest extends ServerWebUiTest{
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeTest.class);
	
	HomeTest(ServerInfoBean infoBean){
		super(infoBean);
	}
	
	@Test
	public void basicLoadTest(){
		LOGGER.info("Loading the home page.");
		this.wrapper.navigateTo("");
		
		LOGGER.info("Loaded the home page.");
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
				(RemoteWebElement)createAccountForm,
				"createAccountEmail",
				"createAccountUsername",
				"createAccountPassword",
				"createAccountPasswordConfirm"
			);
		}
		clearForm(createAccountForm);
		
		TestUser testUser = this.userUtils.setupTestUser();
		//doesn't submit with fine input, except for bad password confirm
		{
			
			createAccountEmailInput.sendKeys(testUser.getEmail());
			createAccountUsernameInput.sendKeys(testUser.getUsername());
			createAccountPasswordInput.sendKeys(testUser.getPlainPassword());
			createAccountPasswordConfirmInput.sendKeys(testUser.getPlainPassword() + "hello world");
			
			submitFormAndAssertElementsInvalid(
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
		
		LOGGER.info("Done.");
	}
}