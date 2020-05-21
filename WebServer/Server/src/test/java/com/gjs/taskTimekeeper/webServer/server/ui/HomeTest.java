package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    }
}