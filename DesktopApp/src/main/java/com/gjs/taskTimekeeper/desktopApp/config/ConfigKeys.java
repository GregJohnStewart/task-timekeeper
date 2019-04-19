package com.gjs.taskTimekeeper.desktopApp.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Keys for accessing configuration.
 */
public enum ConfigKeys {
	//basic config keys, from project.properties
	APP_NAME("app.name"),
	APP_VERSION("app.version"),
	APP_BUILDTIME("app.buildtime"),
	LIB_VERSION("lib.version"),
	//Other config
	CONFIG_FILE("configFile", "TKPR_CONFIG_FILE"),
	SAVE_FILE("saveFile", "TKPR_SAVE_FILE"),
	RUN_MODE("run.mode", "TKPR_MODE"),
	//Running config, for single mode
	SINGLE_MODE_HELP("consoleHelp"),
	//default values
	DEFAULT_CONFIG_FILE("default.configFile", CONFIG_FILE),
	DEFAULT_SAVE_FILE("default.saveFile", SAVE_FILE),
	DEFAULT_RUN_MODE("default.runMode", RUN_MODE);

	public final String key;
	public final String envVar;
	public final ConfigKeys defaultFor;

	ConfigKeys(String key) {
		this.key = key;
		this.envVar = null;
		this.defaultFor = null;
	}

	ConfigKeys(String key, String envVar) {
		this.key = key;
		this.envVar = envVar;
		this.defaultFor = null;
	}

	ConfigKeys(String key, ConfigKeys defaultFor) {
		this.key = key;
		this.envVar = null;
		this.defaultFor = defaultFor;
	}

	public static Collection<ConfigKeys> getKeysWithEnv() {
		List<ConfigKeys> keys = new ArrayList<>();
		for (ConfigKeys key : ConfigKeys.values()) {
			if (key.envVar != null) {
				keys.add(key);
			}
		}
		return keys;
	}

	public static Collection<ConfigKeys> getKeysWithDefaultFor() {
		List<ConfigKeys> keys = new ArrayList<>();
		for (ConfigKeys key : ConfigKeys.values()) {
			if (key.defaultFor != null) {
				keys.add(key);
			}
		}
		return keys;
	}
}
