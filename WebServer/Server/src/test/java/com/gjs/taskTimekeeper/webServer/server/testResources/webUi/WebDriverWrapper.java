package com.gjs.taskTimekeeper.webServer.server.testResources.webUi;

import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.response.ValidatableResponse;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.Closeable;
import java.net.URL;
import java.util.List;

import static com.gjs.taskTimekeeper.webServer.server.testResources.rest.TestRestUtils.newJwtCall;
import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebHelpers.submitForm;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    
    public void init() {
        if(this.driver == null) {
            LOGGER.info("Opening web browser");
            this.driver = new FirefoxDriver(new FirefoxOptions().setHeadless(HEADLESS));
        } else {
            LOGGER.info("Driver already started.");
        }
    }
    
    public WebDriver getDriver() {
        return this.driver;
    }
    
    @Override
    public void close() {
        if(this.getDriver() != null) {
            LOGGER.info("Closing the webpage.");
            this.getDriver().close();
        } else {
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
    
    public WebDriverWrapper navigateToServer(URL url) {
        String path = url.toString();
        
        path = path.substring(path.indexOf("/", 9));
        
        return this.navigateTo(path);
    }
    
    public WebDriverWrapper navigateTo() {
        return this.navigateTo("");
    }
    
    public WebDriverWrapper openNavMenu() {
        //TODO:: determine if already opened
        this.getDriver().findElement(By.id("loginNavText")).click();
        return this;
    }
    
    public WebDriverWrapper login(TestUser testUser) {
        this.openNavMenu();
        WebElement loginForm = this.getDriver().findElement(By.id("navbarLogin"));
        
        WebElement usernameEmailInput = loginForm.findElement(By.name("usernameEmail"));
        WebElement passwordInput = loginForm.findElement(By.name("password"));
        
        usernameEmailInput.sendKeys(testUser.getEmail());
        passwordInput.sendKeys(testUser.getPlainPassword());
        submitForm(loginForm);
        
        this.waitForPageRefreshingFormToComplete(true);
        
        assertEquals(
            testUser.getUsername(),
            this.waitForElement(By.id("navUsername")).getText()
        );
        this.assertLoggedIn(testUser);
        return this;
    }
    
    public void waitForPageLoad() {
        this.getWait().until(
            driver->driver.findElement(By.id(LOADED_FLAG_ID))
        );
    }
    
    public WebDriverWait getWait() {
        return this.getWait(DEFAULT_WAIT_TIMEOUT);
    }
    
    public WebDriverWait getWait(long timeoutSecs) {
        return new WebDriverWait(this.getDriver(), timeoutSecs);
    }
    
    public WebElement waitForElement(By by) {
        return this.getWait().until(
            driver->driver.findElement(by)
        );
    }
    
    public WebDriverWrapper waitForAjaxComplete() {
        try {
            this.getWait().until(
                driver->(boolean)((JavascriptExecutor)driver).executeScript("return jQuery.active == 0")
            );
        } catch(Throwable e) {
            LOGGER.warn("Error when waiting for ajax to finish: \"{}\"", e.getMessage(), e);
            if(!e.getMessage().contains("jQuery is not defined")) {
                throw e;
            }
        }
        return this;
    }
    
    public WebDriverWrapper waitForPageRefreshingFormToComplete(boolean loggedIn) {
        this.waitForAjaxComplete();
        this.waitForPageLoad();
        this.waitForAjaxComplete();
        this.waitForPageLoad();
    
        if(loggedIn) {
            assertNotNull(this.driver.manage().getCookieNamed("loginToken"));
            this.getWait().until(
                driver->!driver.findElement(By.id("navUsername")).getText().equals("")
            );
        }
        return this;
    }
    
    public WebDriverWrapper closeAllMessages() {
        List<WebElement> messageCloseButtons = this.getDriver().findElements(By.className("messageClose"));
        
        for(WebElement curMessageClose : messageCloseButtons) {
            curMessageClose.click();
        }
        this.waitForAjaxComplete();
        return this;
    }
    
    public void assertLoggedOut() {
        this.getWait().until(
            driver->{
                String topText = driver.findElement(By.id("loginNavText")).getText();
                if(
                    topText.contains("Login")
                ) {
                    return driver.findElement(By.id("loginNavText"));
                }
                return null;
            }
        );
        assertNull(this.driver.manage().getCookieNamed("loginToken"));
    }
    
    public void assertLoggedIn(TestUser user) {
        this.getWait().until(
            driver->{
                String topText = driver.findElement(By.id("loginNavText")).getText();
                if(
                    topText.contains("Logged in as: " + user.getUsername())
                ) {
                    return driver.findElement(By.id("loginNavText"));
                }
                return null;
            }
        );
        
        String token = this.driver.manage().getCookieNamed("loginToken").getValue();
        assertNotNull(token);
        ValidatableResponse response = newJwtCall(token).get("/api/user/auth/tokenCheck").then();
        response.statusCode(Response.Status.OK.getStatusCode());
    }
}
