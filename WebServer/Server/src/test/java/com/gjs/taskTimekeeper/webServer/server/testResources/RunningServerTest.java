package com.gjs.taskTimekeeper.webServer.server.testResources;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

public abstract class RunningServerTest {

    @BeforeAll
    public static void startMongo() throws IOException {
        TestMongo.startMongoTestServer();
    }

    @AfterEach
    public void clearMongo() throws IOException, InterruptedException {
        TestMongo.cleanMongo();
    }

    @AfterAll
    public static void stopMongo() {
        TestMongo.stopMongoTestServer();
    }
}
