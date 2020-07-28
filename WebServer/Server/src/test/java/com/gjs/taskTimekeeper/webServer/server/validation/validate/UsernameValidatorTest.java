package com.gjs.taskTimekeeper.webServer.server.validation.validate;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.UsernameValidationException;
import com.gjs.taskTimekeeper.webServer.server.testResources.WebServerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernameValidatorTest extends WebServerTest {
	private static Stream<Arguments> expectValid() {
		return Stream.of(
			Arguments.of(
				"a",
				"a"
			),
			Arguments.of(
				"a0000000000000000001",
				"a0000000000000000001"
			),
			Arguments.of(
				"a<>!@#$%^&*()_+-=[]1",
				"a&lt;&gt;!@#$%^&amp;*()_+-=[]1"
			),
			Arguments.of(
				"a;'':\",./\\$%^&*()_01",
				"a;'':&quot;,./\\$%^&amp;*()_01"
			),
			Arguments.of(
				"\t  \r\na0000000000000000001    ",
				"a0000000000000000001"
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
	
	private UsernameValidator validator = new UsernameValidator();
	
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
	@MethodSource("expectValid")
	void sanitizeTest(String password, String expectedValidated) {
		assertEquals(expectedValidated, this.validator.sanitize(password));
	}
	
	@ParameterizedTest
	@MethodSource("expectInValid")
	void failingValidationTest(String password) {
		Assertions.assertThrows(UsernameValidationException.class, ()->{
			validator.validate(password);
		});
	}
}