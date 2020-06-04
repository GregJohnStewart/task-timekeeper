package com.gjs.taskTimekeeper.webServer.server.testResources.webUi;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.List;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebHelpers.submitForm;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class WebAssertions {

    public static List<WebElement> assertElementsInvalid(
            RemoteWebElement formElement,
            String... expectedInvalidElementIds
    ){
        List<WebElement> invalidElements = formElement.findElementsByCssSelector(":invalid");

        assertEquals(
                expectedInvalidElementIds.length,
                invalidElements.size(),
                "The number of invalid elements did not match the expected amount."
        );

        for(String curExpectedElementId : expectedInvalidElementIds){
            boolean found = false;
            for(WebElement curInvalidElement : invalidElements){
                if(curExpectedElementId.equals(curInvalidElement.getAttribute("id"))){
                    found = true;
                    break;
                }
            }

            if(!found){
                fail("The form element \""+curExpectedElementId+"\" was not found in the form.");
            }
        }

        return invalidElements;
    }

    public static List<WebElement> submitFormAndAssertElementsInvalid(
            RemoteWebElement formElement,
            String... expectedInvalidElementIds
    ){
        submitForm(formElement);
        return assertElementsInvalid(formElement, expectedInvalidElementIds);
    }
}
