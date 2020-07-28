package com.gjs.taskTimekeeper.webServer.server.testResources.webUi;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.List;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.form.FormHelpers.submitForm;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class WebAssertions {
	
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
				log.error(
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
	
	public static void assertPageHasMessage(
		WebDriverWrapper webDriverWrapper,
		String type,
		String expectedHeaderRegex,
		String expectedMessageRegex
	) {
		webDriverWrapper.getWait().until(webDriver->{
			try {
				WebElement pageAlertDiv = webDriverWrapper.getDriver().findElement(By.id("messageDiv"));
				List<WebElement> messages = pageAlertDiv.findElements(By.className("alertMessage"));
				
				for(WebElement messageElement : messages) {
					boolean headerOk = false;
					boolean messageOk = false;
					boolean typeOk = messageElement.getAttribute("class").contains("alert-" + type);
					
					if(expectedHeaderRegex == null) {
						try {
							//require no heading
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
				log.error(
					"Could not find message that matches the expected header and/or message and/or type. Page: ({}) {}",
					webDriverWrapper.getDriver().getCurrentUrl(),
					webDriverWrapper.getDriver().getPageSource()
				);
				//			fail("Could not find message that matches the expected header and/or message and/or type. Page: (" + webDriverWrapper
				//				.getDriver()
				//				.getCurrentUrl() + ") " + webDriverWrapper
				//				.getDriver()
				//				.getPageSource()
				//			);
				return false;
			} catch(StaleElementReferenceException e) {
				return false;
			}
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
