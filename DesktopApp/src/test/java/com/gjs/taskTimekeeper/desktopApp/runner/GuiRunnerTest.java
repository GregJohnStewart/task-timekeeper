package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.managerIO.LocalFile;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

public class GuiRunnerTest {

	@Before
	public void setup() {
		try {
			Configuration.finalizeConfig();
			LocalFile.ensureFilesExistWritable();
		} catch (CmdLineException e) {
			//nothing to do
		}
	}

	@Ignore("Will create the window but not destroy it")
	@Test
	public void run() {
		//figure out how to close automatically for testing
		new GuiRunner().run();
	}
}