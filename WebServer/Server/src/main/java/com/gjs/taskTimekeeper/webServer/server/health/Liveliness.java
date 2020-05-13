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
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Simple liveliness health check.");
    }

    //TODO:: add health check for default certs
}
