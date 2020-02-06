package com.gjs.taskTimekeeper.webServer.server.testResources;

import org.junit.jupiter.api.AfterEach;


public abstract class RunningServerTest {
    @AfterEach
    public void clearMongo() {
        TestMongo.cleanMongo();
    }
}
