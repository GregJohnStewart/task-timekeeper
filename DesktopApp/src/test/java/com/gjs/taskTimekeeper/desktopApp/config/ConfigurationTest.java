package com.gjs.taskTimekeeper.desktopApp.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

import java.lang.reflect.Field;
import java.util.Properties;

public class ConfigurationTest {
    private boolean beforeFinalized;
    private Properties beforeProperties;

    @Before
    public void before() throws NoSuchFieldException, IllegalAccessException {
        Field field = Configuration.class.getDeclaredField("finalized");
        field.setAccessible(true);
        this.beforeFinalized = field.getBoolean(null);
        field = Configuration.class.getDeclaredField("PROPERTIES");
        field.setAccessible(true);
        this.beforeProperties = (Properties) field.get(null);
    }

    @After
    public void after() throws NoSuchFieldException, IllegalAccessException {
        Field field = Configuration.class.getDeclaredField("finalized");
        field.setAccessible(true);
        field.setBoolean(null, this.beforeFinalized);
        field = Configuration.class.getDeclaredField("PROPERTIES");
        field.setAccessible(true);

        field.set(null, this.beforeProperties);
    }

    @Test(expected = IllegalStateException.class)
    public void testTryFinalizeTwice() throws CmdLineException {
        Configuration.finalizeConfig(new String[0]);
        Configuration.finalizeConfig(new String[0]);
    }

    @Test
    public void test() throws CmdLineException {
        Configuration.finalizeConfig(new String[0]);
        // TODO: actually test this class well
    }
}
