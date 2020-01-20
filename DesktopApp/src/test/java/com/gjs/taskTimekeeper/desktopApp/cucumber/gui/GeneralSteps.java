package com.gjs.taskTimekeeper.desktopApp.cucumber.gui;

import com.gjs.taskTimekeeper.desktopApp.cucumber.gui.context.GuiContext;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertNotNull;

public class GeneralSteps extends StepImplementation {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralSteps.class);

    public GeneralSteps(GuiContext guiContext) {
        super(guiContext);
    }

    @Given("^I use the (.*?) data set.$") //works
    public void i_use_the_s_data_set(String dataset) {
        LOGGER.info(() -> "Using the "+dataset+" dataset.");
        this.guiContext.setDataset(dataset);
        //TODO
    }

    @When("the gui is opened")
    public void the_gui_is_opened() {
        //TODO
        assertNotNull(this.guiContext.getDataset());
    }

    @Then("the gui will open without failing")
    public void the_gui_will_open_without_failing() {
        //TODO
        assertNotNull(this.guiContext.getDataset());
    }
}
