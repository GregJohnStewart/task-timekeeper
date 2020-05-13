package com.gjs.taskTimekeeper.webServer.server.health;

import com.gjs.taskTimekeeper.webServer.server.service.DefaultKeysChecker;
import com.google.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;

/**
 * Health check to mak sure it is known if we are or aren't using a packaged key.
 *
 * TODO:: follow up with the builder nonsense: https://github.com/quarkusio/quarkus/issues/9282
 */
@Readiness
@ApplicationScoped
public class DefaultKeysHealthCheck implements HealthCheck {
    private static final String CHECK_NAME = "Not using packaged keys.";

    @Inject
    DefaultKeysChecker defaultKeysChecker;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.builder();

        builder = builder.name(CHECK_NAME);

        if(this.defaultKeysChecker.usingPackagedKeys()){
            builder = builder.down();
            builder = builder.withData("reason", "One or more of the keys used were identified to be the ones packaged with the original project.");
        }else{
            builder = builder.up();
        }

        return builder.build();
    }
}
