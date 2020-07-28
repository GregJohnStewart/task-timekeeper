package com.gjs.taskTimekeeper.desktopApp.config.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringPlaceholderTest {
	
	public static Collection<Object[]> data() {
		LocalDateTime now = LocalDateTime.now();
		
		String home = System.getProperty("user.home");
		String d = StringPlaceholder.DATE_FORMATTER.format(now);
		String t = StringPlaceholder.TIME_FORMATTER.format(now);
		String dt = StringPlaceholder.DATETIME_FORMATTER.format(now);
		
		return Arrays.asList(
			new Object[][]{
				{"", ""},
				{"hello world", "hello world"},
				{"hello {HOME} world", "hello " + home + " world"},
				{"hello {DATE} world", "hello " + d + " world"},
				{"hello {TIME} world", "hello " + t + " world"},
				{"hello {DATETIME} world", "hello " + dt + " world"},
				{"hello {BADPLACEOHOLDER} world", "hello {BADPLACEOHOLDER} world"},
				{"hello {HOME}{HOME} world", "hello " + home + home + " world"},
				{
					"hello {HOME}{DATE}{TIME}{DATETIME} world",
					"hello " + home + d + t + dt + " world"
				},
				{
					"hello {HOME}{DATE}{TIME}{DATETIME}{HOME}{DATE}{TIME}{DATETIME} world",
					"hello " + home + d + t + dt + home + d + t + dt + " world"
				},
				});
	}
	
	@ParameterizedTest
	@MethodSource("data")
	public void testPlaceholder(String withPlaceholder, String expected) {
		assertEquals(expected, StringPlaceholder.processPlaceholders(withPlaceholder));
	}
}
