package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class ObjectsWithStringsAnitizerTest extends AnitizerTest<Object> {
	
	public ObjectsWithStringsAnitizerTest(ObjectWithStringsAnitizer anitizer) {
		super(anitizer);
	}
	
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
				new ObjectWithStrings("hello world", 1),
				new ObjectWithStrings("hello world", 1)
			),
			Arguments.of(
				new ObjectWithStrings("<hello world>", 1),
				new ObjectWithStrings("&lt;hello world&gt;", 1)
			)
		);
	}
	
	@ParameterizedTest
	@MethodSource("args")
	public void testObjectWithStringsAnitizer(Object given, Object expectedSanitized) {
		this.testAnitize(given, expectedSanitized);
	}
	
	@Data
	@AllArgsConstructor
	private static class ObjectWithStrings {
		private String strField;
		private int intField;
	}
}