package com.gjs.taskTimekeeper.desktopApp.config;

import java.lang.reflect.Field;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

public class ConfigurationTest {
    private boolean beforeFinalized;
    private Properties beforeProperties;

    @Before
    public void before() throws NoSuchFieldException, IllegalAccessException {
        Field field = DesktopAppConfiguration.class.getDeclaredField("finalized");
        field.setAccessible(true);
        this.beforeFinalized = field.getBoolean(null);
        field = DesktopAppConfiguration.class.getDeclaredField("PROPERTIES");
        field.setAccessible(true);
        this.beforeProperties = (Properties) field.get(null);
    }

    @After
    public void after() throws NoSuchFieldException, IllegalAccessException {
        Field field = DesktopAppConfiguration.class.getDeclaredField("finalized");
        field.setAccessible(true);
        field.setBoolean(null, this.beforeFinalized);
        field = DesktopAppConfiguration.class.getDeclaredField("PROPERTIES");
        field.setAccessible(true);

        field.set(null, this.beforeProperties);
    }

    @Test(expected = IllegalStateException.class)
    public void testTryFinalizeTwice() throws CmdLineException {
        DesktopAppConfiguration.GLOBAL_CONFIG.finalizeConfig(new String[0]);
        DesktopAppConfiguration.GLOBAL_CONFIG.finalizeConfig(new String[0]);
    }

    @Ignore("Ignoring for travis for some reason.")
    @Test
    public void test() throws CmdLineException {
        DesktopAppConfiguration.GLOBAL_CONFIG.finalizeConfig(new String[0]);
        // TODO: actually test this class well
    }
}
