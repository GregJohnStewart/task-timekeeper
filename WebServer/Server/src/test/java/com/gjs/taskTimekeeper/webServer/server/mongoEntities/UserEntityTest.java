package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class UserEntityTest extends RunningServerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEntityTest.class);

    @Test
    public void testPersistUser(){
        LOGGER.debug("Creating test user.");
        User user = new User();
        LOGGER.debug("Persisting user.");
        user.persist();
        LOGGER.debug("Persisted user.");

        List<User> users = User.listAll();

        assertFalse(users.isEmpty());
        User foundUser = User.findById(user.id);
        assertEquals(user, foundUser);
    }

    @Test
    public void testFindById(){
        User user = new User();
        user.setUsername("testForFindIdUser");
        user.persist();

        User userGotten = User.findById(user.id);
        assertEquals(user, userGotten);
    }

    @Test
    public void testFindByBadId(){
        User userGotten = User.findById(new ObjectId());

        assertNull(userGotten);
    }

    @Test
    public void testFindByEmail(){
        User user = new User();
        user.setUsername("testForFindByEmail");
        user.setEmail("test@test.com");
        user.persist();

        User userGotten = User.findByEmail("test@test.com");

        assertEquals(user, userGotten);
    }

    @Test
    public void testFindByBadEmail(){
        User user = new User();
        user.setUsername("testForFindByBadEmail");
        user.setEmail("test@test.com");
        user.persist();

        assertThrows(EntityNotFoundException.class, ()->{
            User.findByEmail("test@badtest.com");
        });
    }

    @Test
    public void testFindByUsername(){
        User user = new User();
        user.setUsername("testForFindByUsername");
        user.setEmail("test@test.com");
        user.persist();

        User userGotten = User.findByUsername("testForFindByUsername");

        assertEquals(user, userGotten);
    }

    @Test
    public void testFindByBadUsername(){
        User user = new User();
        user.setUsername("testForFindByUsername");
        user.setEmail("test@test.com");
        user.persist();

        assertThrows(EntityNotFoundException.class, ()->{
            User.findByUsername("testForFindByBadUsername");
        });
    }

    @Test
    public void testFindByEmailOrUsername(){
        User user = new User();
        user.setUsername("testForFindByUsernameOrEmail");
        user.setEmail("test@test.com");
        user.persist();

        User userGotten = User.findByEmailOrUsername("testForFindByUsernameOrEmail");

        assertEquals(user, userGotten);

        userGotten = User.findByEmailOrUsername("test@test.com");

        assertEquals(user, userGotten);
    }

    @Test
    public void testFindByBadEmailOrUsername(){
        User user = new User();
        user.setUsername("testForFindByBadUsernameOrEmail");
        user.setEmail("test@test.com");
        user.persist();

        assertThrows(EntityNotFoundException.class, ()->{
            User userGotten = User.findByEmailOrUsername("someBadUsername");
        });
        assertThrows(EntityNotFoundException.class, ()->{
            User userGotten = User.findByEmailOrUsername("test@badEmailDomain.com");
        });
        assertThrows(EntityNotFoundException.class, ()->{
            User userGotten = User.findByEmailOrUsername("bad everything");
        });
    }

}
