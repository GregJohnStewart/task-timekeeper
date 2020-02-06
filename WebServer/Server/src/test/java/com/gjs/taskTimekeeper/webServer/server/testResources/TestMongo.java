package com.gjs.taskTimekeeper.webServer.server.testResources;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
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
    private static MongodExecutable MONGO = null;

    public static void startMongoTestServer() throws IOException {
        if(MONGO != null){
            LOGGER.info("Flapdoodle Mongo already started.");
            return;
        }
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
        if (MONGO == null) {
            LOGGER.warn("Mongo was not started.");
            return;
        }
        MONGO.stop();
        MONGO = null;
    }

    public static void cleanMongo() throws IOException, InterruptedException {
        if(MONGO == null){
            LOGGER.warn("Mongo was not started.");
            return;
        }

        Thread.sleep(1000);
        LOGGER.info("Cleaning Mongo of all entries.");
        LOGGER.info("Deleting {} users.", User.listAll().size());
        User.deleteAll();
    }
}
