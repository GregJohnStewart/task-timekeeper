package com.gjs.taskTimekeeper.webServer.server.testResources.webUi;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WebHelpers {

    public static void clearForm(WebElement formElement){
        formElement.findElement(By.className("resetButton")).click();
    }

    public static void submitForm(WebElement formElement){
        formElement.findElement(By.className("submitButton")).click();
    }
}
