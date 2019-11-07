package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import static org.junit.Assert.*;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSourceTest;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.GuiRunnerTest;
import java.io.File;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

public class CliSingleRunnerTest extends DataSourceTest {
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
        CliSingleRunner runner = new CliSingleRunner(getTestConfig(), "");
        runner.run();
        runner = new CliSingleRunner(getTestConfig(), "-baddddd");
        runner.run();
        // TODO:: decide how we want to test and verify this
    }
}
