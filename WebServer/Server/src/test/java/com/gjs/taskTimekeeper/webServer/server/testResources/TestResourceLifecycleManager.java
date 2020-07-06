package com.gjs.taskTimekeeper.webServer.server.testResources;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.apache.logging.log4j.core.util.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO:: make better with configuration
 */
public class TestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestResourceLifecycleManager.class);
    private static final boolean HEADLESS = false;
    
    private static volatile MongodExecutable MONGO = null;
    private static volatile WebDriver webDriver;
    
    static {
        WebDriverManager.firefoxdriver().setup();
    }
    
    public static synchronized void startMongoTestServer() throws IOException {
        if(MONGO != null) {
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

    public static synchronized void stopMongoTestServer(){
        if (MONGO == null) {
            LOGGER.warn("Mongo was not started.");
            return;
        }
        MONGO.stop();
        MONGO = null;
    }

    public synchronized static void cleanMongo() {
        if(MONGO == null){
            LOGGER.warn("Mongo was not started.");
            return;
        }
    
        LOGGER.info("Cleaning Mongo of all entries.");
        LOGGER.info("Deleting {} users.", User.listAll().size());
        User.deleteAll();
        Assert.isEmpty(User.findAll());
        LOGGER.info("Deleting {} manager entities.", ManagerEntity.listAll().size());
        ManagerEntity.deleteAll();
        Assert.isEmpty(User.findAll());
    }
    
    
    public static synchronized boolean webDriverIsInitted() {
        return getWebDriver() != null;
    }
    
    public static synchronized void initWebDriver() {
        if(!webDriverIsInitted()) {
            LOGGER.info("Opening web browser");
            webDriver = new FirefoxDriver(new FirefoxOptions().setHeadless(HEADLESS));
        } else {
            LOGGER.info("Driver already started.");
        }
    }
    
    public static synchronized WebDriver getWebDriver() {
        return webDriver;
    }
    
    @Override
    public Map<String, String> start() {
        try {
            startMongoTestServer();
        } catch(IOException e) {
            LOGGER.error("Unable to start Flapdoodle Mongo server");
        }
        
        initWebDriver();
        
        return new HashMap<>();
    }

    @Override
    public void stop() {
        stopMongoTestServer();
        LOGGER.info("Closing web driver.");
        getWebDriver().close();
    }
}
