package com.gjs.taskTimekeeper.desktopApp.gui;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.gui.utils.TestFileUtils;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.GuiRunner;
import lombok.extern.slf4j.Slf4j;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;
import java.io.IOException;

@Execution(ExecutionMode.SAME_THREAD)
@Slf4j
public abstract class GuiTest {

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
    
        log.info("Ran gui.");

        while (!this.runner.isMainGuiFinishedLoading()){
            Thread.sleep(250);
        }

        this.fixture = new FrameFixture(this.runner.getMainFrame());
    }


    @AfterEach
    public void ensureClosed() throws InterruptedException {
        this.fixture.cleanUp();
        try{
            this.fixture.close();
        }catch (Exception e){
            log.debug("Closing fixture threw exception: ", e);
        }
        while(this.guiThread.isAlive()){
            runner.closeGuiElements();
            Thread.sleep(100);
        }
    }

    @AfterEach
    public void cleanupWorkingFile() throws IOException {
        log.info("Resetting working file after tests.");
        TestFileUtils.resetWorkingFiles();
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
