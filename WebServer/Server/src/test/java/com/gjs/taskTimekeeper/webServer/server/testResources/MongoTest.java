package com.gjs.taskTimekeeper.webServer.server.testResources;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

public abstract class MongoTest {
    @BeforeAll
    public static void startMongo() throws IOException {
        TestMongo.startMongoTestServer();
    }
    @AfterAll
    public static void stopMongo() {
        TestMongo.stopMongoTestServer();
    }
}
