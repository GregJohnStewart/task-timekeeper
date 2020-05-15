package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class GeneralUiTest extends ServerWebUiTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralUiTest.class);

    public GeneralUiTest(ServerInfoBean infoBean) {
        super(infoBean);
    }

    @Test
    public void notLoggedInUiElements() {
        LOGGER.info("Loading the home page.");
        this.wrapper.navigateTo("");
        LOGGER.info("Loaded the home page.");

        this.wrapper.assertLoggedOut();
        this.wrapper.getDriver().findElement(By.id("navbarLoginContent")).isDisplayed();
    }

    @Test
    public void navbarLogin() {
        //TODO
        LOGGER.info("Loading the home page.");
        this.wrapper.navigateTo("");
        LOGGER.info("Loaded the home page.");

        this.wrapper.getDriver().findElement(By.id("loginNavText")).click();
    }
}
