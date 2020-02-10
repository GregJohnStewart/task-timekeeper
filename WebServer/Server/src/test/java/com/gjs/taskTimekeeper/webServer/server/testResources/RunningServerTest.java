package com.gjs.taskTimekeeper.webServer.server.testResources;

import io.quarkus.mailer.MockMailbox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;


public abstract class RunningServerTest {
    @Inject
    MockMailbox mailbox;

    @BeforeEach
    public void clearMailbox() {
        this.mailbox.clear();
    }

    @AfterEach
    public void clearMongo() {
        TestMongo.cleanMongo();
    }
}
