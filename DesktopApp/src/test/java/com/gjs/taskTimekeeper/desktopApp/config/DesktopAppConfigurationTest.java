package com.gjs.taskTimekeeper.desktopApp.config;

import com.gjs.taskTimekeeper.desktopApp.runner.gui.GuiRunnerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys.CONFIG_FILE;
import static com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys.RUN_MODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Execution(ExecutionMode.SAME_THREAD)
public class DesktopAppConfigurationTest {
	private static final File TEST_USER_CONFIG_FILE =
		new File(
			GuiRunnerTest.class
				.getClassLoader()
				.getResource("test/testUserConfig.properties")
				.getFile());
	
	private static void writeToUserConfigFile(byte[] bytes) {
		try(FileOutputStream os = new FileOutputStream(TEST_USER_CONFIG_FILE);) {
			os.write(bytes);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private DesktopAppConfiguration config;
	
	public static Collection<Object[]> data() {
		return Arrays.asList(
			new Object[][]{
				{
					"", // properties file contents
					new HashMap<String, String>(), // env contents
					new String[]{}, // command line args
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
					new String[]{},
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
					new String[]{"--configFile", TEST_USER_CONFIG_FILE.getPath()},
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
					new String[]{"--mode", RunMode.GUI_SWING.name()},
					new HashMap<ConfigKeys, String>() {
						{
							put(RUN_MODE, RunMode.GUI_SWING.name());
						}
					}
				},
				});
	}
	
	@AfterEach
	public void cleanup() {
		writeToUserConfigFile(new byte[0]);
	}
	
	@ParameterizedTest
	@MethodSource("data")
	public void test(
		String userPropertiesFileContents,
		Map<String, String> envVars,
		String[] cmdLineArgs,
		Map<ConfigKeys, String> entriesToAssert
	) throws CmdLineException {
		writeToUserConfigFile(userPropertiesFileContents.getBytes());
		DesktopAppConfiguration config;
		if(envVars == null) {
			config = new DesktopAppConfiguration(cmdLineArgs);
		} else {
			config = new DesktopAppConfiguration(envVars, cmdLineArgs);
		}
		
		for(Map.Entry<Object, Object> expecting : config.getAllProperties()) {
			assertNotNull(expecting.getValue());
		}
		for(Map.Entry<ConfigKeys, String> expecting : entriesToAssert.entrySet()) {
			assertEquals(expecting.getValue(), config.getProperty(expecting.getKey()));
		}
	}
}
