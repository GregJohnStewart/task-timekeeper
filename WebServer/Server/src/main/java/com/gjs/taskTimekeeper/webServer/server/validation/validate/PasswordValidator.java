package com.gjs.taskTimekeeper.webServer.server.validation.validate;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.PasswordValidationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Getter
@Slf4j
public class PasswordValidator extends StringValidator {
	
	private final int minLength;
	private final boolean includeCaps;
	private final boolean includeSpecialChars;
	private final boolean includeNumbers;
	
	@Inject
	public PasswordValidator(
		@ConfigProperty(name = "validation.password.rules.minLength")
			int minLength,
		@ConfigProperty(name = "validation.password.rules.includeCaps")
			boolean includeCaps,
		@ConfigProperty(name = "validation.password.rules.includeSpecialChars")
			boolean includeSpecialChars,
		@ConfigProperty(name = "validation.password.rules.includeNumbers")
			boolean includeNumbers
	) {
		if(minLength < 4) {
			throw new IllegalArgumentException("Minimum length of password cannot be less than 4. Given: " + minLength);
		}
		this.minLength = minLength;
		this.includeCaps = includeCaps;
		this.includeSpecialChars = includeSpecialChars;
		this.includeNumbers = includeNumbers;
	}
	
	/**
	 * Basic constructor for a "really good" password validation.
	 */
	public PasswordValidator() {
		// DO NOT CHANGE THESE, if changed it will break tests
		this(
			32,
			true,
			true,
			true
		);
	}
	
	
	@Override
	public String sanitize(String object) {
		//don't do anything to passwords
		return object;
	}
	
	@Override
	protected void validate(String object) {
		// https://stackoverflow.com/questions/16127923/checking-letter-case-upper-lower-within-a-string-in-java
		// https://stackoverflow.com/questions/18590901/check-if-a-string-contains-numbers-java
		if(object == null) {
			throw new PasswordValidationException("Password cannot be null.");
		}
		if(this.getMinLength() > object.length()) {
			throw new PasswordValidationException(
				"Password is too short. Must be at least " +
					this.getMinLength() +
					" characters. Was " +
					object.length() +
					" character(s)."
			);
		}
		if(object.equals(object.toUpperCase())) {
			throw new PasswordValidationException("Password must have at least one lower case letter.");
		}
		if(this.isIncludeCaps() && object.equals(object.toLowerCase())) {
			throw new PasswordValidationException("Password must have at least one capitol letter.");
		}
		if(this.isIncludeSpecialChars() && object.matches("[A-Za-z0-9 ]*")) {
			throw new PasswordValidationException("Password must contain at least one special character.");
		}
		if(this.isIncludeNumbers() && !object.matches(".*\\d.*")) {
			throw new PasswordValidationException("Password must contain at least one number.");
		}
	}
}
