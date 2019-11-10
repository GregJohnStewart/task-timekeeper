package com.gjs.taskTimekeeper.desktopApp.config;

import com.gjs.taskTimekeeper.desktopApp.config.exception.SetReadOnlyPropertyException;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

public class DesktopAppConfigurationSupplementalTest {

    @Test(expected = SetReadOnlyPropertyException.class)
    public void setDefaultVal() throws CmdLineException {
        DesktopAppConfiguration config = new DesktopAppConfiguration();

        config.putProperty(ConfigKeys.DEFAULT_CONFIG_FILE, "");
    }
}
