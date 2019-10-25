package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.FileDataSourceTest;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.managerIO.LocalFile;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

public class GuiRunnerTest {
    private final File testFile =
            new File(
                    FileDataSourceTest.class
                            .getClassLoader()
                            .getResource("testTimeManagerData/fully_populated.json")
                            .getFile());

    @Before
    public void setup() {
        Configuration.setFile(ConfigKeys.SAVE_FILE, testFile);
        try {
            Configuration.finalizeConfig();
            LocalFile.ensureFilesExistWritable();
        } catch (CmdLineException e) {
            // nothing to do
        }
    }

    @Test
    public void runGui() {
        // figure out how to close automatically for testing
        new GuiRunner(true).run();
    }
}
