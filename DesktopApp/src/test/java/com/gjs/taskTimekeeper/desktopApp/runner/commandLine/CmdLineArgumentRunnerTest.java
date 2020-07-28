package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import com.gjs.taskTimekeeper.baseCode.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.GuiRunnerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CmdLineArgumentRunnerTest {
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
	public void runExit() throws CmdLineException {
		Assertions.assertThrows(DoExit.class, ()->{
			new CmdLineArgumentRunner(getTestConfig(), false, "-q").run();
		});
	}
	
	@Test
	public void showHelp() throws CmdLineException {
		new CmdLineArgumentRunner(getTestConfig(), false, "-h").run();
		// TODO:: verify help was outputtted
	}
	
	@Test
	public void doSave() throws CmdLineException {
		new CmdLineArgumentRunner(getTestConfig(), false, "-sa").run();
		// TODO:: verify data was saved
	}
	
	@Test
	public void selectLatest() throws CmdLineException {
		ManagerIO io;
		CmdLineArgumentRunner runner = new CmdLineArgumentRunner(getTestConfig(), false);
		io = runner.getManagerIO();
		
		assertNull(io.getManager().getCrudOperator().getSelectedWorkPeriod());
		
		runner.run(true);
		assertNotNull(io.getManager().getCrudOperator().getSelectedWorkPeriod());
		assertEquals(
			io.getManager().getWorkPeriods().first(),
			io.getManager().getCrudOperator().getSelectedWorkPeriod()
		);
	}
}
