package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
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
class HomeTest extends ServerWebUiTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeTest.class);

    HomeTest(ServerInfoBean infoBean) {
        super(infoBean);
    }

    @Test
    public void basicLoadTest() {
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
        WebElement createAccountPasswordConfirmInput = createAccountForm.findElement(By.id("createAccountPasswordConfirm"));
        WebElement createAccountSubmitButton = createAccountForm.findElement(By.id("createAccountSubmitButton"));

        //doesn't submit on no input
        {
            submitFormAndAssertElementsInvalid(
                    (RemoteWebElement) createAccountForm,
                    "createAccountEmail",
                    "createAccountUsername",
                    "createAccountPassword",
                    "createAccountPasswordConfirm"
            );
        }
        clearForm(createAccountForm);

        //doesn't submit with fine input, except for bad password confirm
        {
            createAccountEmailInput.sendKeys(this.userUtils.getTestUserEmail());
            createAccountUsernameInput.sendKeys(this.userUtils.getTestUserUsername());
            createAccountPasswordInput.sendKeys(this.userUtils.getTestUserPassword());
            createAccountPasswordConfirmInput.sendKeys(this.userUtils.getTestUserPassword() + "hello world");

            submitFormAndAssertElementsInvalid(
                    (RemoteWebElement) createAccountForm,
                    "createAccountPasswordConfirm"
            );
        }

        //submits, have new user, got email
        {
            createAccountPasswordConfirmInput.clear();
            createAccountPasswordConfirmInput.sendKeys(this.userUtils.getTestUserPassword());

            createAccountSubmitButton.click();

            this.wrapper.waitForElement(By.id("createAccountSuccessMessage"));

            User testUser = User.findByEmail(this.userUtils.getTestUserEmail());

            List<Mail> sent = mailbox.getMessagesSentTo(testUser.getEmail());
            assertEquals(1, sent.size());
            Mail actual = sent.get(0);

            assertEquals(
                    "Welcome to the TaskTimekeeper Server",
                    actual.getSubject()
            );
        }
        
        LOGGER.info("Done.");
    }

    //TODO:: home login form
}