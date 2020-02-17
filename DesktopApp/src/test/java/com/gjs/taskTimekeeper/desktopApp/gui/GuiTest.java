package com.gjs.taskTimekeeper.desktopApp.gui;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.CliManagerRunnerTest;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.GuiRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public abstract class GuiTest {

    protected static final int MIN_STARTUP_TIME = 1_000;

    private static final Logger LOGGER = LoggerFactory.getLogger(GuiTest.class);

    protected static final File fullTestFile =
            new File(
                    CliManagerRunnerTest.class
                            .getClassLoader()
                            .getResource("testTimeManagerData/fully_populated.json")
                            .getFile());
    protected static final File emptyTestFile =
            new File(
                    CliManagerRunnerTest.class
                            .getClassLoader()
                            .getResource("testTimeManagerData/empty.json")
                            .getFile());

    protected static DesktopAppConfiguration getTestConfig(File file) throws CmdLineException {
        DesktopAppConfiguration config = new DesktopAppConfiguration();

        config.putProperty(ConfigKeys.SAVE_FILE, "file:" + file.getPath());

        return config;
    }


    private Thread guiThread;
    protected GuiRunner runner;
    protected FrameFixture fixture;

    protected void startGui(File dataFile) throws CmdLineException, InterruptedException {
        DesktopAppConfiguration configuration = getTestConfig(dataFile);

        this.runner = new GuiRunner(configuration);

        this.guiThread = new Thread(new GuiRunnable(this.runner));

        this.guiThread.start();

        LOGGER.info("Ran gui.");
        Thread.sleep(MIN_STARTUP_TIME);


        this.fixture = new FrameFixture(this.runner.getMainFrame());
        this.fixture.cleanUp();
    }


    @After
    public void ensureClosed() throws InterruptedException {
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
