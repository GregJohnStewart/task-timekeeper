package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.baseCode.stats.stats.AllStats;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class AllStatsAnitizerTest extends AnitizerTest<AllStats> {
	
	public AllStatsAnitizerTest(AllStatsAnitizer anitizer) {
		super(anitizer);
	}
	
	private static Object[][] args() {
		return new Object[][]{
			new Object[]{null, null}
			//TODO:: more
		};
	}
	
	@ParameterizedTest
	@MethodSource("args")
	public void testAllStatsAnitizer(AllStats given, AllStats expectedSanitized) {
		this.testAnitize(given, expectedSanitized);
	}
}