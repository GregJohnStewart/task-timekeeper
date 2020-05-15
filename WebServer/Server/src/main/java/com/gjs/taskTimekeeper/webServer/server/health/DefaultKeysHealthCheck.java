package com.gjs.taskTimekeeper.webServer.server.health;

import com.gjs.taskTimekeeper.webServer.server.service.DefaultKeysChecker;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * Health check to make sure it is known if we are or aren't using a packaged key.
 */
@Readiness
@ApplicationScoped
public class DefaultKeysHealthCheck implements HealthCheck {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultKeysHealthCheck.class);
    private static final String CHECK_NAME = "Not using packaged keys.";

    private final DefaultKeysChecker defaultKeysChecker;

    public DefaultKeysHealthCheck(DefaultKeysChecker defaultKeysChecker) {
        this.defaultKeysChecker = defaultKeysChecker;
    }

    @Override
    public HealthCheckResponse call() {
        LOGGER.debug("Creating health check response for default keys check.");
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
