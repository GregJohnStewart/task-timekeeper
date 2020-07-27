package com.gjs.taskTimekeeper.desktopApp.gui.utils;

public class GuiCommon {
	private static final long COMMAND_WAIT_TIME = 0;
	
	public static void pauseAfterCommand() throws InterruptedException {
		Thread.sleep(COMMAND_WAIT_TIME);
	}
}
