package com.gjs.taskTimekeeper.desktopApp.gui;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.gui.utils.TestFileUtils;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.GuiRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public abstract class GuiTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiTest.class);

    protected static DesktopAppConfiguration getTestConfig(File file) throws CmdLineException {
        DesktopAppConfiguration config = new DesktopAppConfiguration();

        config.putProperty(ConfigKeys.SAVE_FILE, "file:" + file.getPath());
        config.putProperty(ConfigKeys.CONFIG_FILE, TestFileUtils.testUserConfigFile.getPath());
        config.putProperty(ConfigKeys.UI_OPTIONS_FILE, TestFileUtils.testUiOptionsFile.getPath());

        return config;
    }


    private Thread guiThread;
    protected GuiRunner runner;
    protected FrameFixture fixture;

    protected void startGui(File dataFile) throws Exception {
        DesktopAppConfiguration configuration = getTestConfig(dataFile);

        this.runner = new GuiRunner(configuration);

        this.guiThread = new Thread(new GuiRunnable(this.runner));

        this.guiThread.start();

        LOGGER.info("Ran gui.");

        while (!this.runner.isMainGuiFinishedLoading()){
            Thread.sleep(250);
        }

        this.fixture = new FrameFixture(this.runner.getMainFrame());
    }


    @After
    public void ensureClosed() throws InterruptedException {
        this.fixture.cleanUp();
        try{
            this.fixture.close();
        }catch (Exception e){
            LOGGER.debug("Closing fixture threw exception: ", e);
        }
        while(this.guiThread.isAlive()){
            runner.closeGuiElements();
            Thread.sleep(100);
        }
    }

    @After
    public void cleanupWorkingFile() throws IOException {
        LOGGER.info("Resetting working file after tests.");
        TestFileUtils.resetWorkingFile();
    }


    private static class GuiRunnable implements Runnable {
        private GuiRunner runner;

        public GuiRunnable(GuiRunner runner){
            this.runner = runner;
        }

        public void run() {
            this.runner.run();
        }
    }

}
