package com.gjs.taskTimekeeper.webServer.server.mondoEntities;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.MongoTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class UserEntityTest extends MongoTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEntityTest.class);

    @AfterEach
    public void cleanup() {
        User.deleteAll();
    }

    @Test
    public void testUser(){
        LOGGER.debug("Creating test user.");
        User user = new User();
        LOGGER.debug("Persisting user.");
        user.persist();
        LOGGER.debug("Persisted user.");
    }

}
