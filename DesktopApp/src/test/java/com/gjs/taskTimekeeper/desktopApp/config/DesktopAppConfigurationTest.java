package com.gjs.taskTimekeeper.desktopApp.config;

import static com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys.CONFIG_FILE;
import static com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys.RUN_MODE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.gjs.taskTimekeeper.desktopApp.runner.GuiRunnerTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kohsuke.args4j.CmdLineException;

@RunWith(Parameterized.class)
public class DesktopAppConfigurationTest {
    private static final File TEST_USER_CONFIG_FILE =
            new File(
                    GuiRunnerTest.class
                            .getClassLoader()
                            .getResource("test/testUserConfig.properties")
                            .getFile());

    private static void writeToUserConfigFile(byte[] bytes) {
        try (FileOutputStream os = new FileOutputStream(TEST_USER_CONFIG_FILE); ) {
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DesktopAppConfiguration config;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][] {
                    {
                        "", // properties file contents
                        new HashMap<String, String>(), // env contents
                        new String[] {}, // command line args
                        new HashMap<ConfigKeys, String>() {
                            { // entries to assert
                                put(RUN_MODE, "SINGLE");
                            }
                        }
                    },
                    {
                        RUN_MODE.key + " = " + RunMode.GUI_SWING.name(),
                        new HashMap<String, String>() {
                            {
                                put(CONFIG_FILE.envVar, TEST_USER_CONFIG_FILE.getPath());
                            }
                        },
                        new String[] {},
                        new HashMap<ConfigKeys, String>() {
                            {
                                put(CONFIG_FILE, TEST_USER_CONFIG_FILE.getPath());
                                put(RUN_MODE, RunMode.GUI_SWING.name());
                            }
                        }
                    },
                    {
                        RUN_MODE.key + " = " + RunMode.GUI_SWING.name(),
                        null,
                        new String[] {"--configFile", TEST_USER_CONFIG_FILE.getPath()},
                        new HashMap<ConfigKeys, String>() {
                            {
                                put(CONFIG_FILE, TEST_USER_CONFIG_FILE.getPath());
                                put(RUN_MODE, RunMode.GUI_SWING.name());
                            }
                        }
                    },
                    {
                        "",
                        null,
                        new String[] {"--mode", RunMode.GUI_SWING.name()},
                        new HashMap<ConfigKeys, String>() {
                            {
                                put(RUN_MODE, RunMode.GUI_SWING.name());
                            }
                        }
                    },
                });
    }

    private final String userConfigFileContents;
    private final Map<String, String> envVars;
    private final String[] cmdLineArgs;
    private final Map<ConfigKeys, String> entriesToAssert;

    public DesktopAppConfigurationTest(
            String userPropertiesFileContents,
            Map<String, String> envVars,
            String[] cmdLineArgs,
            Map<ConfigKeys, String> entriesToAssert) {
        this.userConfigFileContents = userPropertiesFileContents;
        this.envVars = envVars;
        this.cmdLineArgs = cmdLineArgs;
        this.entriesToAssert = entriesToAssert;

        this.writeUserConfigFile();
    }

    @Before
    public void writeUserConfigFile() {
        writeToUserConfigFile(this.userConfigFileContents.getBytes());
    }

    @After
    public void cleanup() {
        writeToUserConfigFile(new byte[0]);
    }

    @Test
    public void test() throws CmdLineException {
        DesktopAppConfiguration config;
        if (this.envVars == null) {
            config = new DesktopAppConfiguration(this.cmdLineArgs);
        } else {
            config = new DesktopAppConfiguration(this.envVars, this.cmdLineArgs);
        }

        for (Map.Entry<Object, Object> expecting : config.getAllProperties()) {
            assertNotNull(expecting.getValue());
        }
        for (Map.Entry<ConfigKeys, String> expecting : this.entriesToAssert.entrySet()) {
            assertEquals(expecting.getValue(), config.getProperty(expecting.getKey()));
        }
    }
}
