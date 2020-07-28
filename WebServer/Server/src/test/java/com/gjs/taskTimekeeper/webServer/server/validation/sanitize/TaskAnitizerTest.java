package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.stream.Stream;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class TaskAnitizerTest extends AnitizerTest<Task> {
	
	public TaskAnitizerTest(TaskAnitizer anitizer) {
		super(anitizer);
	}
	
	private static Stream<Arguments> args() {
		return Stream.of(
			Arguments.of(
				null,
				null
			),
			Arguments.of(
				new Task("task"),
				new Task("task")
			),
			Arguments.of(
				new Task("<task>"),
				new Task("&lt;task&gt;")
			),
			Arguments.of(
				new Task("<task>", new HashMap<String, String>() {{
					put("<hello>", "<world>");
					put("<some>", "<BODY>");
				}}),
				new Task("&lt;task&gt;", new HashMap<String, String>() {{
					put("&lt;hello&gt;", "&lt;world&gt;");
					put("&lt;some&gt;", "&lt;BODY&gt;");
				}})
			)
		);
	}
	
	@ParameterizedTest
	@MethodSource("args")
	public void testStringMapAnitizer(Task given, Task expectedSanitized) {
		this.testAnitize(given, expectedSanitized);
	}
}