package com.gjs.taskTimekeeper.webServer.server.health;

import com.gjs.taskTimekeeper.webServer.server.service.DefaultKeysChecker;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;

/**
 * Health check to make sure it is known if we are or aren't using a packaged key.
 */
@Readiness
@ApplicationScoped
@Slf4j
public class DefaultKeysHealthCheck implements HealthCheck {
	public static final String CHECK_NAME = "Not using packaged keys.";
	
	private final DefaultKeysChecker defaultKeysChecker;
	
	public DefaultKeysHealthCheck(DefaultKeysChecker defaultKeysChecker) {
		this.defaultKeysChecker = defaultKeysChecker;
	}
	
	@Override
	public HealthCheckResponse call() {
		log.debug("Creating health check response for default keys check.");
		HealthCheckResponseBuilder builder = HealthCheckResponse.builder();
		
		builder = builder.name(CHECK_NAME);
		
		if(this.defaultKeysChecker.usingPackagedKeys()) {
			builder = builder.down();
			builder = builder.withData(
				"reason",
				"One or more of the keys used were identified to be the ones packaged with the original project."
			);
		} else {
			builder = builder.up();
		}
		
		return builder.build();
	}
}
