package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.MainSystemTray;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.forms.MainGui;
import java.awt.Image;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiRunner extends ModeRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiRunner.class);

    private static final Image ICON;
    private static final String APP_TITLE;
    private static final long WAIT_TIME = 100;
    private static final long AUTOCLOSE_WAIT_LOOPS = 25;

    static {
        try (InputStream is =
                GuiRunner.class
                        .getClassLoader()
                        .getResourceAsStream(
                                Configuration.GLOBAL_CONFIG.getProperty(
                                        ConfigKeys.STATIC_GUI_ICON, String.class))) {
            if (is == null) {
                throw new IllegalStateException("Desktop icon image not found.");
            }
            ICON = ImageIO.read(is);
        } catch (Exception e) {
            LOGGER.error("FAILED to read in icon image. Error: ", e);
            throw new RuntimeException(e);
        }

        APP_TITLE =
                "Task Timekeeper v"
                        + Configuration.GLOBAL_CONFIG.getProperty(
                                ConfigKeys.APP_VERSION, String.class);

        LOGGER.debug("Setup gui static resources.");
    }

    private final boolean autoClose;
    private MainGui mainGui;
    private MainSystemTray systemTray;

    public GuiRunner() {
        this.autoClose = false;
    }

    public GuiRunner(boolean autoClose) {
        this.autoClose = autoClose;
    }

    @Override
    public void run() {
        LOGGER.info("Running the GUI mode.");
        runMainGui();
        runSystemTrayIcon();
        LOGGER.debug("Running UI components");

        long count = 0;
        while (this.stillRunning()) {
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                LOGGER.warn("Wait interrupted.", e);
            }
            if (this.autoClose && count > AUTOCLOSE_WAIT_LOOPS) {
                this.closeGuiElements();
            }
            count++;
        }
        LOGGER.info("All UI components closed. Exiting.");
    }

    private void runMainGui() {
        this.mainGui = new MainGui(ICON, APP_TITLE);
    }

    private void runSystemTrayIcon() {
        this.systemTray = new MainSystemTray(ICON, APP_TITLE);
    }

    public boolean stillRunning() {
        return this.mainGui.stillOpen() || this.systemTray.stillRunning();
    }

    public void closeGuiElements() {
        this.mainGui.close();
    }
}
