package com.gjs.taskTimekeeper.webServer.server.service.mongo;

import com.gjs.taskTimekeeper.webServer.server.pojo.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.MongoTest;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * TODO: update to use property collection, use that test collection
 * TODO:: make parameterized to test many types/ collections
 */
@QuarkusTest
public class UserServiceTest extends MongoTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceTest.class);

	@Inject
	private UserService userService;

	private ObjectId id = ObjectId.get();

	@AfterEach
	public void cleanup() {
		this.userService.removeAll();
	}


	@Test
	public void testUserService() {
		User testUser = new User();
		testUser.set_id(id);
		testUser.setUsername("testUser");
		testUser.setHashedPass("hashed password");

		userService.add(testUser);


		List<User> userList = userService.list();

		LOGGER.debug("# users in list: {}", userList.size());
		LOGGER.debug("user list: {}", userList);
		assertEquals(1, userList.size());
		assertEquals(testUser, userList.get(0));


		User userReturned = userService.get(id);
		assertNotNull(userReturned);
		assertEquals(testUser, userReturned);

		LOGGER.info("Passed test!");
	}
}
