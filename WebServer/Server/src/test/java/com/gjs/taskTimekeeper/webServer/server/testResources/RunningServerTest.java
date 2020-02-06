package com.gjs.taskTimekeeper.webServer.server.testResources;

import org.junit.jupiter.api.AfterEach;

import java.io.IOException;


public abstract class RunningServerTest {
    @AfterEach
    public void clearMongo() throws IOException, InterruptedException {
        TestMongo.cleanMongo();
    }
}
