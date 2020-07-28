package com.gjs.taskTimekeeper.webServer.server.validation.validate;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.PasswordValidationException;
import com.gjs.taskTimekeeper.webServer.server.testResources.WebServerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class PasswordValidatorTest extends WebServerTest {
	private static Stream<Arguments> expectValid() {
		return Stream.of(
			Arguments.of(
				new PasswordValidator(),
				"aA1!0000000000000000000000000001"
			)
		);
	}
	
	private static Stream<Arguments> expectInValid() {
		return Stream.of(
			//fails null
			Arguments.of(
				new PasswordValidator(),
				null
			),
			//fails length
			Arguments.of(
				new PasswordValidator(),
				"aA1!000000000000000000000000000"
			),
			//fails lower case
			Arguments.of(
				new PasswordValidator(),
				"EA1!0000000000000000000000000001"
			),
			//fails upper case
			Arguments.of(
				new PasswordValidator(),
				"ae1!0000000000000000000000000001"
			),
			//fails number
			Arguments.of(
				new PasswordValidator(),
				"aAe!eeeeeeeeeeeeeeeeeeeeeeeeeeee"
			),
			//fails special char
			Arguments.of(
				new PasswordValidator(),
				"aA1e0000000000000000000000000001"
			),
			Arguments.of(
				new PasswordValidator(),
				"badpass"
			)
		);
	}
	
	@Test
	void cantHaveTooShortPasswordTest() {
		Assertions.assertThrows(IllegalArgumentException.class, ()->{
			new PasswordValidator(3, true, true, true);
		});
	}
	
	@ParameterizedTest
	@MethodSource("expectValid")
	void passingValidationTest(PasswordValidator validator, String password) {
		Assertions.assertDoesNotThrow(()->{
			validator.validate(password);
		});
	}
	
	@ParameterizedTest
	@MethodSource("expectInValid")
	void failingValidationTest(PasswordValidator validator, String password) {
		Assertions.assertThrows(PasswordValidationException.class, ()->{
			validator.validate(password);
		});
	}
}