package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
@Slf4j
public class UserEntityTest extends RunningServerTest {
	
	@Test
	public void testPersistUser() {
		log.debug("Creating test user.");
		User user = new User();
		log.debug("Persisting user.");
		user.persist();
		log.debug("Persisted user.");
		
		List<User> users = User.listAll();
		
		assertFalse(users.isEmpty());
		User foundUser = User.findById(user.id);
		assertEquals(user, foundUser);
	}
	
	@Test
	public void testFindById() {
		User user = new User();
		user.setUsername("testForFindIdUser");
		user.persist();
		
		User userGotten = User.findById(user.id);
		assertEquals(user, userGotten);
	}
	
	@Test
	public void testFindByBadId() {
		User userGotten = User.findById(new ObjectId());
		
		assertNull(userGotten);
	}
	
	@Test
	public void testFindByEmail() {
		User user = new User();
		user.setUsername("testForFindByEmail");
		user.setEmail("testForFindByEmail@test.com");
		user.persist();
		
		User userGotten = User.findByEmail("testForFindByEmail@test.com");
		
		assertEquals(user, userGotten);
	}
	
	@Test
	public void testFindByBadEmail() {
		User user = new User();
		user.setUsername("testForFindByBadEmail");
		user.setEmail("testFindByBadEmail@test.com");
		user.persist();
		
		assertThrows(EntityNotFoundException.class, ()->{
			User.findByEmail("testForFindByBadEmail@badtest.com");
		});
	}
	
	@Test
	public void testFindByUsername() {
		User user = new User();
		user.setUsername("testForFindByUsername");
		user.setEmail("testForFindByUsername@test.com");
		user.persist();
		
		User userGotten = User.findByUsername("testForFindByUsername");
		
		assertEquals(user, userGotten);
	}
	
	@Test
	public void testFindByBadUsername() {
		User user = new User();
		user.setUsername("testForFindByBadUsername");
		user.setEmail("testForFindByBadUsername@test.com");
		user.persist();
		
		assertThrows(EntityNotFoundException.class, ()->{
			User.findByUsername("testForFindByBadUsernameBaaad");
		});
	}
	
	@Test
	public void testFindByEmailOrUsername() {
		User user = new User();
		user.setUsername("testForFindByUsernameOrEmail");
		user.setEmail("testForFindByUsernameOrEmail@test.com");
		user.persist();
		
		User userGotten = User.findByEmailOrUsername("testForFindByUsernameOrEmail");
		
		assertEquals(user, userGotten);
		
		userGotten = User.findByEmailOrUsername("testForFindByUsernameOrEmail@test.com");
		
		assertEquals(user, userGotten);
	}
	
	@Test
	public void testFindByBadEmailOrUsername() {
		User user = new User();
		user.setUsername("testForFindByBadUsernameOrEmail");
		user.setEmail("testForFindByBadUsernameOrEmail@test.com");
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
	
	
	@Test
	public void testEquals() {
		User entityOne = new User();
		User entityTwo = new User();
		
		assertEquals(
			entityOne,
			entityTwo
		);
		
		entityOne.persist();
		entityTwo.persist();
		
		assertNotEquals(
			entityOne,
			entityTwo
		);
		//TODO:: more
	}
}
