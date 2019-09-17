package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.ActionDoer;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.OutputLevel;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.MainGui;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.MainSystemTray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;

public class GuiRunner extends ModeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(GuiRunner.class);

	private static final Image ICON;
	private static final String APP_TITLE;
	private static final long WAIT_TIME = 100;

	static {
		try(InputStream is = GuiRunner.class.getClassLoader().getResourceAsStream(
			Configuration.getProperty(ConfigKeys.STATIC_GUI_ICON, String.class)
		)
		){
			if(is == null){
				throw new IllegalStateException("Desktop icon image not found.");
			}
			ICON = ImageIO.read(is);
		}catch (Exception e){
			LOGGER.error("FAILED to read in icon image. Error: ", e);
			throw new RuntimeException(e);
		}

		APP_TITLE = "Task Timekeeper v" + Configuration.getProperty(ConfigKeys.APP_VERSION, String.class);
		LOGGER.debug("Setup gui static resources.");
	}

	private MainGui mainGui;
	private MainSystemTray systemTray;

	@Override
	public void run() {
		LOGGER.info("Running the GUI mode.");
		ActionDoer.setConsoleOutputLevel(OutputLevel.NONE);
		runMainGui();
		runSystemTrayIcon();
		LOGGER.debug("Running UI components");

		while(this.stillRunning()){
			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				LOGGER.warn("Wait interrupted.", e);
			}
			LOGGER.trace("Waiting for UI components to close.");
		}
		LOGGER.info("All UI components closed. Exiting.");
	}

	private void runMainGui(){
		this.mainGui = new MainGui(ICON, APP_TITLE);
	}

	private void runSystemTrayIcon(){
		this.systemTray = new MainSystemTray(ICON, APP_TITLE);
	}

	public boolean stillRunning(){
		return mainGui.stillOpen();
	}
}
