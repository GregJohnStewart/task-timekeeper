package com.gjs.taskTimekeeper.webServer.server;

import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.time.Duration;
import java.time.ZonedDateTime;

@ApplicationScoped
public class LifecycleBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleBean.class);

    private ZonedDateTime startDateTime;

    //TODO:: come back to this when working.
//    @Inject
    @ResourcePath("startTemplate")
    Template startTemplate;

    @ConfigProperty(name = "version")
    String serverVersion;
    @ConfigProperty(name = "buildtime")
    String buildtime;
    @ConfigProperty(name = "lib.base.core.version")
    String coreVersion;
    @ConfigProperty(name = "lib.base.managerIO.version")
    String managerIOVersion;
    @ConfigProperty(name = "lib.base.stats.version")
    String statsVersion;
    @ConfigProperty(name = "lib.server.webLibrary.version")
    String webLibVersion;

    void onStart(@Observes StartupEvent ev) {
        this.startDateTime = ZonedDateTime.now();
        LOGGER.info("Task Timekeeper Web Server starting.");
        LOGGER.debug("Version: {}", this.serverVersion);
        LOGGER.debug("build time: {}", this.buildtime);
        LOGGER.debug("Core lib version: {}", this.coreVersion);
        LOGGER.debug("ManagerIO lib version: {}", this.managerIOVersion);
        LOGGER.debug("Stats lib version: {}", this.statsVersion);
        LOGGER.debug("Web lib version: {}", this.webLibVersion);

        LOGGER.info("start template: {}", this.startTemplate);
        System.out.println(
                this.startTemplate
                        .data("serverVersion", this.serverVersion)
                        .data("buildTime", this.buildtime)
                        .data("coreVersion", this.coreVersion)
                        .data("ioVersion", this.managerIOVersion)
                        .data("statsVersion", this.statsVersion)
                        .data("webVersion", this.webLibVersion)
                        .render()
        );
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The server is stopping.");
        Duration runtime = Duration.between(this.startDateTime, ZonedDateTime.now());
        LOGGER.info("Server ran for {}", TimeParser.toDurationStringExact(runtime));
    }

}
