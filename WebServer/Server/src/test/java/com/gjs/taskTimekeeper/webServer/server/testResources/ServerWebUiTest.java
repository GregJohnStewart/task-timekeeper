package com.gjs.taskTimekeeper.webServer.server.testResources;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import io.quarkus.test.common.http.TestHTTPResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

@Execution(ExecutionMode.SAME_THREAD)// can't run ui test multithreaded using shared window
public abstract class ServerWebUiTest extends RunningServerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerWebUiTest.class);
    
    protected final ServerInfoBean infoBean;
    
    protected WebDriverWrapper wrapper;
    
    @TestHTTPResource("/")
    URL baseUrl;
    
    protected ServerWebUiTest(ServerInfoBean infoBean, WebDriverWrapper wrapper) {
        this.infoBean = infoBean;
        this.wrapper = wrapper;
    }
    
    @BeforeEach
    public void setupDriver() {
        LOGGER.info("Setting up the driver wrapper's base url.");
        this.wrapper.setBaseUrl(baseUrl);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        this.wrapper.getDriver().manage().deleteAllCookies();
        this.wrapper.getDriver().get("about:blank");
    }
}
