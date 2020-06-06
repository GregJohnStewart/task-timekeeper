package com.gjs.taskTimekeeper.webServer.server.testResources.webUi;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class WebDriverWrapper implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverWrapper.class);
    private static final String LOADED_FLAG_ID = "loadedFlag";
    public static final long DEFAULT_WAIT_TIMEOUT = 10;
    private static final boolean HEADLESS = true;

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
            this.driver = new FirefoxDriver(new FirefoxOptions().setHeadless(HEADLESS));
        }else{
            LOGGER.info("Driver already started.");
        }
    }

    public WebDriver getDriver(){
        return this.driver;
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
    
    public WebDriverWrapper navigateTo(String url) {
        this.init();
        this.getDriver().get(this.urlBase + url);
        
        this.waitForPageLoad();
        
        LOGGER.info("\"{}\" page loaded successfully.", url);
        
        return this;
    }
    
    public void waitForPageLoad(){
        this.getWait().until(
            driver -> driver.findElement(By.id(LOADED_FLAG_ID))
        );
    }

    public WebDriverWait getWait(){
        return this.getWait(DEFAULT_WAIT_TIMEOUT);
    }

    public WebDriverWait getWait(long timeoutSecs){
        return new WebDriverWait(this.getDriver(), timeoutSecs);
    }

    public WebElement waitForElement(By by){
        return this.getWait().until(
                driver -> driver.findElement(by)
        );
    }
    
    public WebDriverWrapper waitForAjaxComplete(){
        try{
            this.getWait().until(
                driver->(boolean)((JavascriptExecutor)driver).executeScript("return jQuery.active == 0")
            );
        }catch(Throwable e){
            LOGGER.warn("Error when waiting for ajaxto finish: \"{}\"", e.getMessage(), e);
            if(!e.getMessage().equals("ReferenceError: jQuery is not defined")){
                throw e;
            }
        }
        return this;
    }
    
    public WebDriverWrapper waitForPageRefreshingFormToComplete(boolean loggedIn){
        this.waitForAjaxComplete();
        this.waitForPageLoad();
        
        if(loggedIn){
            this.getWait().until(
                driver->!driver.findElement(By.id("navUsername")).getText().equals("")
            );
        }
        return this;
    }

    public void assertLoggedOut(){
        this.getWait().until(
                driver -> {
                    String topText = driver.findElement(By.id("loginNavText")).getText();
                    if(
                           topText.contains("Login")
                    ) {
                        return driver.findElement(By.id("loginNavText"));
                    }
                    return null;
                }
        );
    }
}
