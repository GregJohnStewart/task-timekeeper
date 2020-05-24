package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebAssertions.assertElementsInvalid;

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
        User testUser = this.userUtils.setupTestUser(false);

        WebElement createAccountForm = this.wrapper.getDriver().findElement(By.id("homeCreateAccount"));

        WebElement createAccountEmailInput = createAccountForm.findElement(By.id("createAccountEmail"));
        WebElement createAccountUsernameInput = createAccountForm.findElement(By.id("createAccountUsername"));
        WebElement createAccountPasswordInput = createAccountForm.findElement(By.id("createAccountPassword"));
        WebElement createAccountPasswordConfirmInput = createAccountForm.findElement(By.id("createAccountPasswordConfirm"));
        WebElement createAccountSubmitButton = createAccountForm.findElement(By.id("createAccountSubmitButton"));

        //doesn't submit on no input
        createAccountSubmitButton.click();

        assertElementsInvalid(
                (RemoteWebElement) createAccountForm,
                "createAccountEmail",
                "createAccountUsername",
                "createAccountPassword",
                "createAccountPasswordConfirm"
        );

        createAccountForm.clear();

        //TODO:: continue tests

        //doesn't submit
        LOGGER.info("Done.");
    }
}