package com.gjs.taskTimekeeper.webServer.server.validation.validate;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.EmailValidationException;
import com.gjs.taskTimekeeper.webServer.server.testResources.WebServerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class EmailValidatorTest extends WebServerTest {
	private static Stream<Arguments> expectValid() {
		return Stream.of(
			Arguments.of(
				"a@b.cd"
			)
		);
	}
	
	private static Stream<Arguments> expectInValid() {
		return Stream.of(
			//fails null
			Arguments.of(
				(String)null
			),
			Arguments.of(
				" \t\n\r"
			),
			Arguments.of(
				"a000 00000001"
			),
			Arguments.of(
				"a000\n00000001"
			),
			Arguments.of(
				"a000\r00000001"
			),
			Arguments.of(
				"a000\t00000001"
			)
		);
	}
	
	private EmailValidator validator = new EmailValidator();
	
	@ParameterizedTest
	@MethodSource("expectValid")
	void passingValidationTest(String password) {
		Assertions.assertDoesNotThrow(()->{
			this.validator.validate(
				password
			);
		});
	}
	
	@ParameterizedTest
	@MethodSource("expectInValid")
	void failingValidationTest(String password) {
		Assertions.assertThrows(EmailValidationException.class, ()->{
			validator.validate(password);
		});
	}
}