package com.gjs.taskTimekeeper.webServer.server.service.mongo;

import com.gjs.taskTimekeeper.webServer.server.pojo.User;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * TODO: update to use property collection, use that test collection
 */
@QuarkusTest
public class UserServiceTest {
	@Inject
	private UserService userService;

	private ObjectId id = ObjectId.get();

	@AfterEach
	public void cleanup() {
		this.userService.removeAll();
	}

	@Test
	public void test() {
		User testUser = new User();
		testUser.setId(id);
		testUser.setUsername("testUser");
		testUser.setHashedPass("hashed password");
		testUser.setRoles(new ArrayList<>());

		userService.add(testUser);


		List<User> userList = userService.list();

		User userReturned = userService.get(id);
		assertNotNull(userReturned);
		assertEquals(testUser, userReturned);
	}
}