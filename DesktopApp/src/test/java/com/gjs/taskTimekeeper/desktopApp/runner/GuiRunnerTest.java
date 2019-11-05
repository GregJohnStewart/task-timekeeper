package com.gjs.taskTimekeeper.desktopApp.runner;

import java.io.File;

/** TODO:: figure out how to properly test Swing apps */
public class GuiRunnerTest {
    private final File testFile =
            new File(
                    GuiRunnerTest.class
                            .getClassLoader()
                            .getResource("testTimeManagerData/fully_populated.json")
                            .getFile());

    //    @Before
    //    public void setup() {
    //        try {
    //            DesktopAppConfiguration.GLOBAL_CONFIG.finalizeConfig();
    //        } catch (CmdLineException e) {
    //            // nothing to do
    //        }
    //        DesktopAppConfiguration.GLOBAL_CONFIG.setFile(ConfigKeys.SAVE_FILE, testFile);
    //    }
    //
    //    @Ignore(value = "FIX")
    //    @Test
    //    public void runGui() {
    //        // figure out how to close automatically for testing
    //        new GuiRunner(true).run();
    //    }
}
