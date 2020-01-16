package com.gjs.taskTimekeeper.desktopApp.cucumber;

import com.gjs.taskTimekeeper.desktopApp.Main;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CucumberTestSteps {
    @Given("that we want to test with cucumber")
    public void thatWeWantToTestWithCucumber() {
    }

    @When("we run the cucumber task")
    public void weRunTheCucumberTask() {
    }

    @Then("this all runs")
    public void thisAllRuns() {
    }

    @And("we cover lines of code not covered in unit tests")
    public void weCoverLinesOfCodeNotCoveredInUnitTests() {
        Main.forCucumberTestPleaseDontCall();
    }
}
