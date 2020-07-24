package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Inject;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class HTMLAnitizerTest extends RunningServerTest {
	
	@Inject
	HTMLAnitizer sanitizer;
	
	private static Stream<Arguments> args() {
		return Stream.of(
			Arguments.of(
				null,
				null
			),
			Arguments.of(
				"hello world",
				"hello world"
			),
			Arguments.of(
				"<hello world>",
				"&lt;hello world&gt;"
			),
			Arguments.of(
				"&lt;hello world&gt;",
				"&amp;lt;hello world&amp;gt;"
			),
			Arguments.of(
				"& lt;hello world& gt;",
				"&amp; lt;hello world&amp; gt;"
			),
			Arguments.of(
				"\"hello world\"",
				"&quot;hello world&quot;"
			)
		);
	}
	
	@ParameterizedTest
	@MethodSource("args")
	public void testHtmlSanitizer(String given, String expectedSanitized) {
		String sanitized = sanitizer.sanitize(given);
		
		assertEquals(expectedSanitized, sanitized);
		
		String deSanitized = sanitizer.deSanitize(sanitized);
		
		assertEquals(given, deSanitized);
	}
}