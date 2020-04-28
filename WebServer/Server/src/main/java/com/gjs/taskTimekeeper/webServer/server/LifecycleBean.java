package com.gjs.taskTimekeeper.webServer.server;

import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import com.google.inject.Inject;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
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
    private static final String PACKAGED_PRIVATE = "packagedPrivateKey.pem";
    private static final String PACKAGED_PUBLIC = "packagedPublicKey.pem";
    public static final String PACKAGED_KEYS_ERR_MESSAGE = "Using packaged keys. This is unacceptable, see the Admin Guide on the project's github for more information.";
    public static final String PACKAGED_KEYS_ERR_MESSAGE_LINE_2 = "Server will automatically shut down in 10 minutes.";
    public static final String PACKAGED_KEYS_CLOSE_MESSAGE = "Using packaged keys. This is unacceptable, see the Admin Guide on the project's github for more information.";

    private ZonedDateTime startDateTime;

    @Inject
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

    @ConfigProperty(name="mp.jwt.verify.privatekey.location")
    String privateKeyLocation;
    @ConfigProperty(name="mp.jwt.verify.publickey.location")
    String publicKeyLocation;

    void onStart(@Observes StartupEvent ev) throws InterruptedException {
        this.startDateTime = ZonedDateTime.now();
        LOGGER.info("Task Timekeeper Web Server starting.");
        LOGGER.debug("Version: {}", this.serverVersion);
        LOGGER.debug("build time: {}", this.buildtime);
        LOGGER.debug("Core lib version: {}", this.coreVersion);
        LOGGER.debug("ManagerIO lib version: {}", this.managerIOVersion);
        LOGGER.debug("Stats lib version: {}", this.statsVersion);
        LOGGER.debug("Web lib version: {}", this.webLibVersion);

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
        if(this.usingPackagedKeys()){
            Thread.sleep(100);
            System.err.println(PACKAGED_KEYS_ERR_MESSAGE);
            System.err.println(PACKAGED_KEYS_ERR_MESSAGE_LINE_2);

            LOGGER.error(PACKAGED_KEYS_ERR_MESSAGE);
            LOGGER.error(PACKAGED_KEYS_ERR_MESSAGE_LINE_2);
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The server is stopping.");
        Duration runtime = Duration.between(this.startDateTime, ZonedDateTime.now());
        LOGGER.info("Server ran for {}", TimeParser.toDurationStringExact(runtime));
    }

    private boolean usingPackagedKeys(){
        return this.privateKeyLocation.contains(PACKAGED_PRIVATE) ||
                this.publicKeyLocation.contains(PACKAGED_PUBLIC);
    }

    @Scheduled(every = "1m", delay = 10)
    void bootIfPackagedKeys() {
        if(this.usingPackagedKeys()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.err.println(PACKAGED_KEYS_CLOSE_MESSAGE);
                    LOGGER.error(PACKAGED_KEYS_CLOSE_MESSAGE);
                    System.exit(1);
                }
            }).start();
        }
    }
}
