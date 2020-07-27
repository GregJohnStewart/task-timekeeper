package com.gjs.taskTimekeeper.webServer.server.service;

import com.gjs.taskTimekeeper.webServer.server.exception.request.user.CorruptedKeyException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.IncorrectPasswordException;
import com.gjs.taskTimekeeper.webServer.server.exception.validation.PasswordValidationException;
import com.gjs.taskTimekeeper.webServer.server.testResources.WebServerTest;
import com.gjs.taskTimekeeper.webServer.server.validation.validate.PasswordValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordServiceTest extends WebServerTest {
	public static final String GOOD_PASS = "aA1!0000000000000000000000000001";
	private PasswordService passwordService = new PasswordService(new PasswordValidator());
	
	@Test
	void createPasswordHash() {
		String hashed = passwordService.createPasswordHash(GOOD_PASS);
		assertNotNull(hashed);
	}
	
	@Test
	void createPasswordHashBadPass() {
		assertThrows(PasswordValidationException.class, ()->{
			passwordService.createPasswordHash("badPass");
		});
	}
	
	@Test
	void passwordMatchesHash() {
		String hashed = passwordService.createPasswordHash(GOOD_PASS);
		
		assertTrue(passwordService.passwordMatchesHash(hashed, GOOD_PASS));
	}
	
	@Test
	void passwordNotMatchesHash() {
		String hashed = passwordService.createPasswordHash(GOOD_PASS);
		
		assertFalse(passwordService.passwordMatchesHash(hashed, "wrong pass"));
	}
	
	@Test
	void corruptedKey() {
		assertThrows(CorruptedKeyException.class, ()->{
			passwordService.passwordMatchesHash("some bad hash", "wrong pass");
		});
	}
	
	@Test
	void assertPasswordMatchesHash() {
		String hashed = passwordService.createPasswordHash(GOOD_PASS);
		
		assertDoesNotThrow(()->{
			passwordService.assertPasswordMatchesHash(hashed, GOOD_PASS);
		});
	}
	
	@Test
	void assertPasswordNotMatchesHash() {
		String hashed = passwordService.createPasswordHash(GOOD_PASS);
		
		assertThrows(IncorrectPasswordException.class, ()->{
			passwordService.assertPasswordMatchesHash(hashed, "wrong password");
		});
	}
}