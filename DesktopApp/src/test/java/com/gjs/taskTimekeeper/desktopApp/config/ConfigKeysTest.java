package com.gjs.taskTimekeeper.desktopApp.config;

import com.gjs.taskTimekeeper.desktopApp.config.exception.ConfigKeyDoesNotExistException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigKeysTest {
	
	@Test
	public void getKeyOf() {
		assertEquals(ConfigKeys.APP_NAME, ConfigKeys.getKeyOf("app.name"));
	}
	
	@Test
	public void getKeyOfBadKey() {
		Assertions.assertThrows(ConfigKeyDoesNotExistException.class, ()->{
			ConfigKeys.getKeyOf("app.nameded");
		});
	}
	
	@Test
	public void getKeysWithEnv() {
		for(ConfigKeys curKey : ConfigKeys.getKeysWithEnv()) {
			assertNotNull(curKey.envVar);
		}
	}
	
	@Test
	public void getKeysWithDefaultFor() {
		for(ConfigKeys curKey : ConfigKeys.getKeysWithDefaultFor()) {
			assertNotNull(curKey.defaultFor);
		}
	}
}
