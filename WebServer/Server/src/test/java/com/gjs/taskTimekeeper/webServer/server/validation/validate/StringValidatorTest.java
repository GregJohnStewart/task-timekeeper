package com.gjs.taskTimekeeper.webServer.server.validation.validate;

import com.gjs.taskTimekeeper.webServer.server.testResources.WebServerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringValidatorTest extends WebServerTest {
	private StringValidator stringValidator = new StringValidator();
	
	@Test
	public void testStringValidate() {
		this.stringValidator.validate(null);
		this.stringValidator.validate("");
		this.stringValidator.validate("hello");
		this.stringValidator.validate("world");
	}
	
	@ParameterizedTest
	@MethodSource("sanitizeParams")
	public void testStringSanitize(String toSanitize, String expected) {
		String sanitized = this.stringValidator.sanitize(toSanitize);
		
		assertEquals(expected, sanitized);
	}
	
	public static Stream<Arguments> sanitizeParams() {
		return Stream.of(
			//fails null
			Arguments.of(
				(String)null,
				(String)null
			),
			Arguments.of(
				"",
				""
			)
		);
	}
}
