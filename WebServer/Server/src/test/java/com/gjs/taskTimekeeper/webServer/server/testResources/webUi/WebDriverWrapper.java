package com.gjs.taskTimekeeper.webServer.server.testResources.webUi;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebDriverWrapper implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverWrapper.class);
    private static final String LOADED_FLAG_ID = "loadedFlag";

    static {
        WebDriverManager.firefoxdriver().setup();
    }

    private WebDriver driver = null;
    private final String urlBase;

    public WebDriverWrapper(String urlBase) {
        this.urlBase = urlBase;
    }

    public void init(){
        if(this.driver == null){
            LOGGER.info("Opening web browser");
            this.driver = new FirefoxDriver();
        }else{
            LOGGER.info("Driver already started.");
        }
    }

    public WebDriver getDriver(){
        return this.driver;
    }

    public WebDriverWrapper navigateTo(String url) {
        this.init();
        this.getDriver().get(this.urlBase + url);

        WebDriverWait pageLoadWait = new WebDriverWait(this.getDriver(), 10);

        pageLoadWait.until(
                driver -> driver.findElement(By.id(LOADED_FLAG_ID))
        );

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {

        }

        LOGGER.info("\"{}\" loaded successfully.", url);

        return this;
    }

    @Override
    public void close() {
        if(this.getDriver() != null){
            LOGGER.info("Closing the webpage.");
            this.getDriver().close();
        }else {
            LOGGER.debug("Web browser already closed or wasn't opened.");
        }
    }

    public void assertLoggedOut(){
        assertEquals(
                "Login",
                this.getDriver().findElement(By.id("loginNavText")).getText()
        );
    }
}
