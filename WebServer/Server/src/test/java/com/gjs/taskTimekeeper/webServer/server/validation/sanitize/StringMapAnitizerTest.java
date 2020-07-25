package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class StringMapAnitizerTest extends AnitizerTest<Map<String, String>> {
	
	public StringMapAnitizerTest(StringMapAnitizer anitizer) {
		super(anitizer);
	}
	
	private static Stream<Arguments> args() {
		return Stream.of(
			Arguments.of(
				null,
				null
			),
			Arguments.of(
				new HashMap<String, String>() {{
					put("hello", "world");
					put("some", "BODY");
				}},
				new HashMap<String, String>() {{
					put("hello", "world");
					put("some", "BODY");
				}}
			),
			Arguments.of(
				new HashMap<String, String>() {{
					put("<hello>", "<world>");
					put("<some>", "<BODY>");
				}},
				new HashMap<String, String>() {{
					put("&lt;hello&gt;", "&lt;world&gt;");
					put("&lt;some&gt;", "&lt;BODY&gt;");
				}}
			)
		);
	}
	
	@ParameterizedTest
	@MethodSource("args")
	public void testStringMapAnitizer(Map<String, String> given, Map<String, String> expectedSanitized) {
		this.testAnitize(given, expectedSanitized);
	}
}