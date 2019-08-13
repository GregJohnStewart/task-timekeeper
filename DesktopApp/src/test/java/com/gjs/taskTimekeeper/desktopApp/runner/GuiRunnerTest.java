package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

public class GuiRunnerTest {

	@Before
	public void setup() {
		try {
			Configuration.finalizeConfig();
		} catch (CmdLineException e) {
			//nothing to do
		}
	}

	@Test
	public void run() {
		new GuiRunner().run();
	}
}