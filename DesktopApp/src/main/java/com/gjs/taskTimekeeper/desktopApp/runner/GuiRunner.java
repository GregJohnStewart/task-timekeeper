package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.ActionDoer;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.OutputLevel;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.MainGui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;

public class GuiRunner extends ModeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(GuiRunner.class);

	private static final Image ICON;
	private static final String APP_TITLE;

	static {
		try(InputStream is = GuiRunner.class.getClassLoader().getResourceAsStream(
			Configuration.getProperty(ConfigKeys.STATIC_GUI_ICON, String.class)
		)
		){
			ICON = ImageIO.read(is);
		}catch (Exception e){
			LOGGER.error("FAILED to read in icon image. Error: ", e);
			throw new RuntimeException(e);
		}

		APP_TITLE = "Task Timekeeper v" + Configuration.getProperty(ConfigKeys.APP_VERSION, String.class);
	}

	@Override
	public void run() {
		LOGGER.info("Running the GUI mode.");
		ActionDoer.setConsoleOutputLevel(OutputLevel.NONE);
		MainGui.main(ICON, APP_TITLE);
	}
}
