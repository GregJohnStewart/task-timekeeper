package com.gjs.taskTimekeeper.webServer.server;

import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import io.quarkus.qute.Engine;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.Duration;
import java.time.ZonedDateTime;

@ApplicationScoped
public class LifecycleBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleBean.class);

    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;

    //TODO:: come back to this when working.
//    @Inject
//    @ResourcePath("startTemplate.txt")
//    Template startTemplate;

    @Inject
    Engine engine;

    void onStart(@Observes StartupEvent ev) {
        this.startDateTime = ZonedDateTime.now();
        LOGGER.info("The server is starting...");
        //TODO:: finish
//        System.out.println(
//                this.startTemplate
//                        .data("version", "")
//                        .render()
//        );
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The server is stopping...");
        this.endDateTime = ZonedDateTime.now();
        Duration runtime = Duration.between(this.startDateTime, this.endDateTime);
        LOGGER.info("Server ran for {}", TimeParser.toDurationString(runtime));
    }

}
