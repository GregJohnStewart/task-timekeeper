package com.gjs.taskTimekeeper.webServer.server.testResources;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import io.quarkus.test.common.http.TestHTTPResource;
import org.junit.jupiter.api.BeforeEach;

import java.net.URL;

public abstract class ServerWebUiTest extends RunningServerTest {

    protected final ServerInfoBean infoBean;

    protected WebDriverWrapper wrapper;

    @TestHTTPResource("/")
    URL baseUrl;

    protected ServerWebUiTest(ServerInfoBean infoBean) {
        this.infoBean = infoBean;
    }

    @BeforeEach
    public void setupDriver(){
        this.wrapper = new WebDriverWrapper(baseUrl.toString());
    }

    @Override
    public void cleanup(){
        super.cleanup();

        if(wrapper != null){
            wrapper.close();
        }
    }
}
