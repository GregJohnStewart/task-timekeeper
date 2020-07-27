package com.gjs.taskTimekeeper.webServer.server.validation.validate;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.exception.validation.EmailValidationException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;

import javax.enterprise.context.ApplicationScoped;
import java.util.regex.Pattern;

@ApplicationScoped
public class EmailValidator extends StringValidator {
	
	//https://www.geeksforgeeks.org/check-email-address-valid-not-java/
	private static final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
		"[a-zA-Z0-9_+&*-]+)*@" +
		"(?:[a-zA-Z0-9-]+\\.)+[a-z" +
		"A-Z]{2,7}$";
	
	private static final Pattern pattern = Pattern.compile(emailRegex);
	
	@Override
	public String sanitize(String object) {
		//don't do anything to emails
		return object;
	}
	
	@Override
	protected void validate(String object) {
		if(object == null) {
			throw new EmailValidationException("Email cannot be null.");
		}
		if(!pattern.matcher(object).matches()) {
			throw new EmailValidationException("Received an invalid email.");
		}
	}
	
	public void assertDoesntExist(String validatedSanitized) {
		try {
			User.findByEmail(validatedSanitized);
			throw new EmailValidationException("Email already exists.");
		} catch(EntityNotFoundException e) {
			// nothing to do
		}
	}
	
	public String validateSanitizeAssertDoesntExist(String object) {
		String validated = this.validateAndSanitize(object);
		this.assertDoesntExist(object);
		return validated;
	}
}
