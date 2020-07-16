package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class TimemanagerAnitizerTest extends RunningServerTest {
	
	TimemanagerAnitizer sanitizer;
	
	public TimemanagerAnitizerTest(TimemanagerAnitizer sanitizer) {
		this.sanitizer = sanitizer;
	}
	
	private static TimeManager emptyTimeManager = new TimeManager();
	
	private static Object[][] args() {
		return new Object[][]{
			new Object[]{null, null},
			//TODO:: https://github.com/quarkusio/quarkus/issues/10540
			//			new Object[]{emptyTimeManager.clone(), emptyTimeManager.clone()},
		};
	}
	
	@ParameterizedTest
	@MethodSource("args")
	public void testTimeManagerSanitizer(TimeManager given, TimeManager expectedSanitized) {
		TimeManager sanitized = sanitizer.sanitize(given);
		
		assertEquals(expectedSanitized, sanitized);
		
		TimeManager deSanitized = sanitizer.deSanitize(sanitized);
		
		assertEquals(given, deSanitized);
	}
}