package com.gjs.taskTimekeeper.desktopApp.config;

import static org.junit.Assert.*;

import com.gjs.taskTimekeeper.desktopApp.config.exception.ConfigKeyDoesNotExistException;
import org.junit.Test;

public class ConfigKeysTest {

    @Test
    public void getKeyOf() {
        assertEquals(ConfigKeys.APP_NAME, ConfigKeys.getKeyOf("app.name"));
    }

    @Test(expected = ConfigKeyDoesNotExistException.class)
    public void getKeyOfBadKey() {
        ConfigKeys.getKeyOf("app.nameded");
    }

    @Test
    public void getKeysWithEnv() {
        for (ConfigKeys curKey : ConfigKeys.getKeysWithEnv()) {
            assertNotNull(curKey.envVar);
        }
    }

    @Test
    public void getKeysWithDefaultFor() {
        for (ConfigKeys curKey : ConfigKeys.getKeysWithDefaultFor()) {
            assertNotNull(curKey.defaultFor);
        }
    }
}
