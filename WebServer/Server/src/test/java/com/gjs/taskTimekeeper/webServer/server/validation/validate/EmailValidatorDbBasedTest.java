package com.gjs.taskTimekeeper.webServer.server.validation.validate;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.EmailValidationException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class EmailValidatorDbBasedTest extends RunningServerTest {
	private EmailValidator validator = new EmailValidator();
	
	@Test
	public void assertValidAndDoesntExistTest() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		
		//check
		Assertions.assertDoesNotThrow(()->{
			this.validator.validateSanitizeAssertDoesntExist(this.userUtils.setupTestUser().getEmail());
		});
	}
	
	@Test
	public void addNewUserDuplicateEmailTest() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		
		//check
		Assertions.assertThrows(EmailValidationException.class, ()->{
			this.validator.validateSanitizeAssertDoesntExist(testUser.getEmail());
		});
	}
}