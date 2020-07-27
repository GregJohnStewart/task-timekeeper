package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.TimeManagerResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class TimeManagerResponseAnitizerTest extends AnitizerTest<TimeManagerResponse> {
	
	public TimeManagerResponseAnitizerTest(TimemanagerResponseAnitizer anitizer) {
		super(anitizer);
	}
	
	private static Stream<Arguments> args() {
		return Stream.of(
			Arguments.of(
				null,
				null
			),
			Arguments.of(
				new TimeManagerResponse(),
				new TimeManagerResponse()
			)
			//TODO:: https://github.com/quarkusio/quarkus/issues/10540
			//			new Object[]{emptyTimeManager.clone(), emptyTimeManager.clone()},
		);
	}
	
	@ParameterizedTest
	@MethodSource("args")
	public void testStringMapAnitizer(TimeManagerResponse given, TimeManagerResponse expectedSanitized) {
		this.testAnitize(given, expectedSanitized);
	}
}