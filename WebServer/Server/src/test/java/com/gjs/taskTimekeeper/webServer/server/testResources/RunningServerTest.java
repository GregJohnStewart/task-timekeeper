package com.gjs.taskTimekeeper.webServer.server.testResources;

import io.quarkus.mailer.MockMailbox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;


public abstract class RunningServerTest {
    @Inject
    MockMailbox mailbox;

    @BeforeEach
    public void beforeEach() {
        this.cleanup();
    }

    @AfterEach
    public void afterEach() {
        this.cleanup();
    }

    public void cleanup(){
        this.mailbox.clear();
        TestMongo.cleanMongo();
    }
}
