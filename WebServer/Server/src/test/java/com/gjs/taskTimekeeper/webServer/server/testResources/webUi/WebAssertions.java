package com.gjs.taskTimekeeper.webServer.server.testResources.webUi;

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
}
