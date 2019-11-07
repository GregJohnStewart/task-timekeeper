package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import static org.junit.Assert.*;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.GuiRunnerTest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

/** TODO:: write better */
public class CliManagerRunnerTest {
    private static final File testFile =
            new File(
                    GuiRunnerTest.class
                            .getClassLoader()
                            .getResource("testTimeManagerData/fully_populated.json")
                            .getFile());

    private static DesktopAppConfiguration getTestConfig() throws CmdLineException {
        DesktopAppConfiguration config = new DesktopAppConfiguration();

        config.putProperty(ConfigKeys.SAVE_FILE, "file:" + testFile.getPath());

        return config;
    }

    @Test
    public void run() throws CmdLineException {
        InputStream is = new ByteArrayInputStream("-q\n".getBytes());

        CliManagerRunner runner = new CliManagerRunner(getTestConfig(), is);

        runner.run();
    }
}
