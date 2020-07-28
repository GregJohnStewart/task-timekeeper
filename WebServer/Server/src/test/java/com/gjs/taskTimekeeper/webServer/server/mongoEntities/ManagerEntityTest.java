package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
public class ManagerEntityTest extends RunningServerTest {
	
	//TODO:: more?
	
	@Test
	public void testEquals() {
		ManagerEntity entityOne = new ManagerEntity();
		ManagerEntity entityTwo = new ManagerEntity();
		
		
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
