package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
public class UserEntityTest extends RunningServerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEntityTest.class);

    @Test
    public void testUser(){
        LOGGER.debug("Creating test user.");
        User user = new User();
        LOGGER.debug("Persisting user.");
        user.persist();
        LOGGER.debug("Persisted user.");

        List<User> users = User.listAll();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

}
