package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AnitizerTest <T> extends RunningServerTest {
	
	protected final Anitizer<T> anitizer;
	
	protected AnitizerTest(Anitizer<T> anitizer) {
		this.anitizer = anitizer;
	}
	
	protected void testAnitize(T original, T expectedSanitized) {
		T sanitized = anitizer.sanitize(original);
		assertEquals(expectedSanitized, sanitized);
		T deSanitized = anitizer.deSanitize(sanitized);
		assertEquals(original, deSanitized);
	}
}
