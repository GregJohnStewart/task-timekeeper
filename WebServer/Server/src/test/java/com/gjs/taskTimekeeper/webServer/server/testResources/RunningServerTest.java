package com.gjs.taskTimekeeper.webServer.server.testResources;

import com.gjs.taskTimekeeper.webServer.server.testResources.entity.UserUtils;
import io.quarkus.mailer.MockMailbox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;

//@Execution(ExecutionMode.CONCURRENT)
public abstract class RunningServerTest {
    @Inject
    protected MockMailbox mailbox;
    @Inject
    protected UserUtils userUtils;

    @BeforeEach
    public void beforeEach() {
        this.cleanup();
    }

    @AfterEach
    public void afterEach() {
        this.cleanup();
    }

    public void cleanup(){

    }

    public void cleanupDatabaseAndMail(){
        this.mailbox.clear();
        TestMongo.cleanMongo();
    }
}
