package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import java.io.File;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

/** TODO:: figure out how to properly test Swing apps */
public class GuiRunnerTest {
    private final File testFile =
            new File(
                    GuiRunnerTest.class
                            .getClassLoader()
                            .getResource("testTimeManagerData/fully_populated.json")
                            .getFile());

    @Before
    public void setup() {
        try {
            Configuration.GLOBAL_CONFIG.finalizeConfig();
        } catch (CmdLineException e) {
            // nothing to do
        }
        Configuration.GLOBAL_CONFIG.setFile(ConfigKeys.SAVE_FILE, testFile);
    }

    @Ignore(value = "FIX")
    @Test
    public void runGui() {
        // figure out how to close automatically for testing
        new GuiRunner(true).run();
    }
}
