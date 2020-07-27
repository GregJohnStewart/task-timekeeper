package com.gjs.taskTimekeeper.desktopApp.gui.utils;

import org.assertj.swing.fixture.FrameFixture;

import static com.gjs.taskTimekeeper.desktopApp.gui.utils.GuiCommon.pauseAfterCommand;

public class GuiNavigation {
	
	// <editor-fold desc="Menu items">
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
	
	// </editor-fold>
	// <editor-fold desc="Tabs">
	public static void selectTab(FrameFixture fixture, String name, String tabText) throws InterruptedException {
		fixture.tabbedPane(name).selectTab(tabText);
		pauseAfterCommand();
	}
	
	public static void selectMainTab(FrameFixture fixture, String tabText) throws InterruptedException {
		selectTab(fixture, "mainTabPane", tabText);
	}
	
	public static void selectSelectedPeriodTab(FrameFixture fixture) throws InterruptedException {
		selectMainTab(fixture, "Selected Period");
	}
	
	public static void selectPeriodsTab(FrameFixture fixture) throws InterruptedException {
		selectMainTab(fixture, "Periods");
	}
	
	public static void selectTasksTab(FrameFixture fixture) throws InterruptedException {
		selectMainTab(fixture, "Tasks");
	}
	
	public static void selectStatsTab(FrameFixture fixture) throws InterruptedException {
		selectMainTab(fixture, "Stats");
	}
	// </editor-fold>
}
