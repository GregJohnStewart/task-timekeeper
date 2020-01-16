package com.gjs.taskTimekeeper.desktopApp.runner.gui;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.CliManagerRunnerTest;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;

/** TODO:: figure out how to properly test Swing apps */
public class GuiRunnerTest {
    private static final File fullTestFile =
            new File(
                    CliManagerRunnerTest.class
                            .getClassLoader()
                            .getResource("testTimeManagerData/fully_populated.json")
                            .getFile());
    private static final File emptyTestFile =
            new File(
                    CliManagerRunnerTest.class
                            .getClassLoader()
                            .getResource("testTimeManagerData/empty.json")
                            .getFile());

    private static DesktopAppConfiguration getTestConfig(File file) throws CmdLineException {
        DesktopAppConfiguration config = new DesktopAppConfiguration();

        config.putProperty(ConfigKeys.SAVE_FILE, "file:" + file.getPath());

        return config;
    }

    @Test
    public void runGuiPopulated() throws CmdLineException {
        try {
            new GuiRunner(getTestConfig(fullTestFile), true).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runGuiEmpty() throws CmdLineException {
        new GuiRunner(getTestConfig(emptyTestFile), true).run();
    }


}
