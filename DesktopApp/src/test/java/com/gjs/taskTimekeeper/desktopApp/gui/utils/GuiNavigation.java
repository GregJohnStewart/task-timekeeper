package com.gjs.taskTimekeeper.desktopApp.gui.utils;

import org.assertj.swing.fixture.FrameFixture;

import static com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiCommon.pauseAfterCommand;

public class GuiNavigation {

    public static void clickMenuItem(FrameFixture fixture, String name) throws InterruptedException {
        fixture.menuItem(name).click();
        pauseAfterCommand();
    }

    public static void clickMenuFile(FrameFixture fixture) throws InterruptedException {
        clickMenuItem(fixture, "fileMenu");
    }

    public static void clickMenuInfo(FrameFixture fixture) throws InterruptedException {
        clickMenuItem(fixture, "infoMenu");
    }
}
