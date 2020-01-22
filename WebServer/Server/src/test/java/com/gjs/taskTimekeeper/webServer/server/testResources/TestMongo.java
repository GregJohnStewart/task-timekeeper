package com.gjs.taskTimekeeper.webServer.server.testResources;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * TODO:: make better with configuration
 */
public class TestMongo {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestMongo.class);
    private static MongodExecutable MONGO;

    public static void startMongoTestServer() throws IOException {
        Version.Main version = Version.Main.V4_0;
        int port = 27018;
        LOGGER.info("Starting Flapdoodle Test Mongo {} on port {}", version, port);
        IMongodConfig config = new MongodConfigBuilder()
                .version(version)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();
        MONGO = MongodStarter.getDefaultInstance().prepare(config);
        MONGO.start();
    }

    public static void stopMongoTestServer(){
        if (MONGO != null) {
            MONGO.stop();
        }
    }
}
