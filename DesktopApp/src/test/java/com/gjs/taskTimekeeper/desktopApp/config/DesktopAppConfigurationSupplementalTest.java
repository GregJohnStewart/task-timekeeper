package com.gjs.taskTimekeeper.desktopApp.config;

import com.gjs.taskTimekeeper.desktopApp.config.exception.SetReadOnlyPropertyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.CmdLineException;

public class DesktopAppConfigurationSupplementalTest {
	
	@Test
	public void setDefaultVal() throws CmdLineException {
		DesktopAppConfiguration config = new DesktopAppConfiguration();
		
		Assertions.assertThrows(SetReadOnlyPropertyException.class, ()->{
			config.putProperty(ConfigKeys.DEFAULT_CONFIG_FILE, "");
		});
	}
}
