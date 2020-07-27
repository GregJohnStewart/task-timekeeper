package com.gjs.taskTimekeeper.webServer.server.validation.validate;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.UsernameValidationException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class UsernameValidatorDbBasedTest extends RunningServerTest {
	private UsernameValidator validator = new UsernameValidator();
	
	@Test
	public void addNewUserNameTest() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		
		//check
		Assertions.assertDoesNotThrow(()->{
			this.validator.validateSanitizeAssertDoesntExist(this.userUtils.setupTestUser().getUsername());
		});
	}
	
	@Test
	public void addNewUserDuplicateNameTest() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		
		//check
		Assertions.assertThrows(UsernameValidationException.class, ()->{
			this.validator.validateSanitizeAssertDoesntExist(testUser.getUsername());
		});
	}
}