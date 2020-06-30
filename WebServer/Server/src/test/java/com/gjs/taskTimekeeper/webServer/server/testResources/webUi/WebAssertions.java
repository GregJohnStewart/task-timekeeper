package com.gjs.taskTimekeeper.webServer.server.testResources.webUi;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebHelpers.submitForm;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class WebAssertions {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebAssertions.class);
	
	public static List<WebElement> assertElementsInvalid(
		String identifyingAttribute,
		RemoteWebElement formElement,
		String... expectedInvalidElements
	) {
		List<WebElement> invalidElements = formElement.findElementsByCssSelector(":invalid");
		
		assertEquals(
			expectedInvalidElements.length,
			invalidElements.size(),
			"The number of invalid elements did not match the expected amount."
		);
		
		for(String curExpectedElement : expectedInvalidElements) {
			boolean found = false;
			for(WebElement curInvalidElement : invalidElements) {
				if(curExpectedElement.equals(curInvalidElement.getAttribute(identifyingAttribute))) {
					found = true;
					break;
				}
			}
			
			if(!found) {
				LOGGER.error(
					"Form element with {} as its {} was not found in the set of invalid elements: {}",
					curExpectedElement,
					identifyingAttribute,
					invalidElements
				);
				fail("The form element \"" + curExpectedElement + "\" was not found in the form.");
			}
		}
		
		return invalidElements;
	}
	
	public static List<WebElement> submitFormAndAssertElementsInvalid(
		String identifyingAttribute,
		RemoteWebElement formElement,
		String... expectedInvalidElementIds
	) {
		submitForm(formElement);
		return assertElementsInvalid(identifyingAttribute, formElement, expectedInvalidElementIds);
	}
	
	//TODO:: assert message in general
	
	public static void assertPageHasMessage(
		WebDriverWrapper webDriverWrapper,
		String type,
		String expectedHeaderRegex,
		String expectedMessageRegex
	) {
		webDriverWrapper.getWait().until(webDriver->{
			WebElement pageAlertDiv = webDriverWrapper.getDriver().findElement(By.id("messageDiv"));
			List<WebElement> messages = pageAlertDiv.findElements(By.className("alertMessage"));
			
			for(WebElement messageElement : messages) {
				boolean headerOk = false;
				boolean messageOk = false;
				boolean typeOk = messageElement.getAttribute("class").contains("alert-" + type);
				
				if(expectedHeaderRegex == null) {
					try {
						String headerText = messageElement.findElement(By.className("alert-heading")).getText();
						headerOk = false;
					} catch(NoSuchElementException e) {
						headerOk = true;
					}
				} else {
					try {
						String headerText = messageElement.findElement(By.className("alert-heading")).getText();
						headerOk = expectedHeaderRegex.matches(headerText);
					} catch(NoSuchElementException e) {
						headerOk = false;
					}
				}
				
				String messageText = messageElement.findElement(By.className("message")).getText();
				messageOk = expectedMessageRegex.matches(messageText);
				
				if(headerOk && messageOk && typeOk) {
					return messageElement;
				}
			}
			fail("Could not find message that matches the expected header and/or message and/or type.");
			return false;
		});
	}
	
	public static void assertFormErrorMessage(
		WebElement formElement,
		String expectedMessageRegex,
		String expectedHeaderRegex
	) {
		List<WebElement> messages = formElement.findElements(By.className("alertMessage"));
		
		assertEquals(
			1,
			messages.size()
		);
		
		for(WebElement message : messages) {
			boolean headerOk = false;
			boolean messageOk = false;
			
			if(expectedHeaderRegex != null) {
				String header = message.findElement(By.className("alert-heading")).getText();
				headerOk = expectedHeaderRegex.matches(header);
			} else {
				headerOk = true;
			}
			
			String messageText = message.findElement(By.className("message")).getText();
			messageOk = expectedMessageRegex.matches(messageText);
			
			if(headerOk && messageOk) {
				return;
			}
		}
		fail("Could not find message that matches the expected header and/or message.");
	}
	
	public static void submitFormAndAssertFormErrorMessage(
		WebDriverWrapper wrapper,
		WebElement formElement,
		String expectedMessageRegex,
		String expectedHeaderRegex
	) {
		submitForm(formElement);
		wrapper.waitForAjaxComplete();
		assertFormErrorMessage(formElement, expectedMessageRegex, expectedHeaderRegex);
	}
}
