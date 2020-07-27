package com.gjs.taskTimekeeper.webServer.server.service;

import com.gjs.taskTimekeeper.webServer.server.testResources.WebServerTest;
import com.gjs.taskTimekeeper.webServer.server.validation.validate.PasswordValidator;
import org.junit.jupiter.api.Test;

public class TokenServiceTest extends WebServerTest {
	
	//TODO:: more
	
	private PasswordValidator validator = new PasswordValidator();
	private TokenService service = new TokenService(this.validator);
	
	@Test
	public void testTokenService() {
		String token = service.generateToken();
		
		this.validator.validateAndSanitize(token);
	}
}