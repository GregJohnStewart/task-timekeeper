package com.gjs.taskTimekeeper.webServer.server.testResources.webUi.form;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FormHelpers {
	public static void resetForm(WebElement formElement) {
		formElement.findElement(By.className("resetButton")).click();
	}
	
	public static void submitForm(WebElement formElement) {
		formElement.findElement(By.className("submitButton")).click();
	}
}
