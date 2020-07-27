package com.gjs.taskTimekeeper.desktopApp.runner.gui;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.ModeRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.forms.MainGui;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class GuiRunner extends ModeRunner {
	private final Image ICON;
	private final String APP_TITLE;
	private final long WAIT_TIME = 100;
	private final long AUTOCLOSE_WAIT_LOOPS = 25;
	private boolean autoClose = false;
	private boolean mainGuiFinishedLoading;
	private MainGui mainGui;
	private MainSystemTray systemTray;
	
	public GuiRunner(DesktopAppConfiguration config) {
		super(config);
		
		try(
			InputStream is =
				GuiRunner.class
					.getClassLoader()
					.getResourceAsStream(this.config.getProperty(ConfigKeys.STATIC_GUI_ICON))
		) {
			if(is == null) {
				throw new IllegalStateException("Desktop icon image not found.");
			}
			ICON = ImageIO.read(is);
		} catch(Exception e) {
			log.error("FAILED to read in icon image. Error: ", e);
			throw new RuntimeException(e);
		}
		
		APP_TITLE = "Task Timekeeper v" + this.config.getProperty(ConfigKeys.APP_VERSION);
		
		log.debug("Setup gui static resources.");
	}
	
	public GuiRunner(DesktopAppConfiguration config, boolean autoClose) {
		this(config);
		this.autoClose = autoClose;
	}
	
	@Override
	public void run() {
		log.info("Running the GUI mode.");
		runMainGui();
		runSystemTrayIcon();
		log.debug("Running UI components");
		
		long count = 0;
		while(this.stillRunning()) {
			try {
				Thread.sleep(WAIT_TIME);
			} catch(InterruptedException e) {
				log.warn("Wait interrupted.", e);
			}
			if(this.autoClose && count > AUTOCLOSE_WAIT_LOOPS) {
				this.closeGuiElements();
			}
			count++;
		}
		log.info("All UI components closed. Exiting.");
	}
	
	private void runMainGui() {
		log.info("Running main GUI component.");
		this.mainGuiFinishedLoading = false;
		
		try {
			SwingUtilities.invokeAndWait(()->mainGui = new MainGui(config, ICON, APP_TITLE));
		} catch(InterruptedException e) {
			log.error("Wait for invocation to run gui interrupted. ", e);
		} catch(InvocationTargetException e) {
			log.error("", e);
		}
		//TODO:: check if mainGui is null
		this.mainGuiFinishedLoading = true;
		
		log.debug("Main GUI component ran.");
	}
	
	private void runSystemTrayIcon() {
		log.info("Running system tray component.");
		this.systemTray = new MainSystemTray(ICON, APP_TITLE);
		log.debug("System tray component ran.");
	}
	
	/**
	 * Determines if any gui elements are still running.
	 *
	 * @return If any gui elements are still running.
	 */
	public boolean stillRunning() {
		return this.mainGui.stillOpen() || this.systemTray.stillRunning();
	}
	
	/**
	 * Closes all gui elements.
	 */
	public void closeGuiElements() {
		log.info("Closing all ui elements.");
		this.mainGui.close();
		this.mainGuiFinishedLoading = false;
		log.debug("Closed all ui elements.");
	}
	
	public boolean isMainGuiFinishedLoading() {
		return this.mainGuiFinishedLoading;
	}
	
	public JFrame getMainFrame() {
		return this
			.mainGui
			.getMainFrame();
	}
}
