package com.gjs.taskTimekeeper.webServer.server.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;

/**
 * https://quarkus.io/guides/microprofile-health
 */
@Liveness
@ApplicationScoped
public class Liveliness implements HealthCheck {
	public static final String SIMPLE_LIVELINESS_HEALTH_CHECK = "Simple liveliness check.";
	
	@Override
	public HealthCheckResponse call() {
		return HealthCheckResponse.up(SIMPLE_LIVELINESS_HEALTH_CHECK);
	}
}
