package com.gjs.taskTimekeeper.desktopApp.cucumber.gui;

import com.gjs.taskTimekeeper.desktopApp.cucumber.gui.context.GuiContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StepImplementation {
    private static final Logger LOGGER = LoggerFactory.getLogger(StepImplementation.class);

    public GuiContext guiContext;

    public StepImplementation(GuiContext guiContext){
        this.guiContext = guiContext;
    }
}
