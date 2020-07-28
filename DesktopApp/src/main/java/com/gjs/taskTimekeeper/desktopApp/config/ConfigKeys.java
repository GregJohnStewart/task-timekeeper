package com.gjs.taskTimekeeper.desktopApp.config;

import com.gjs.taskTimekeeper.desktopApp.config.exception.ConfigKeyDoesNotExistException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Keys for accessing configuration.
 */
public enum ConfigKeys {
	// basic config keys, from project.properties
	APP_NAME(true, "app.name"),
	APP_VERSION(true, "app.version"),
	APP_BUILDTIME(true, "app.buildtime"),
	LIB_VERSION(true, "lib.version"),
	// Other config
	CONFIG_FILE(false, "configFile", "TKPR_CONFIG_FILE"),
	UI_OPTIONS_FILE(false, "uiOptionsFile", "TKPR_UI_CONFIG_FILE"),
	SAVE_FILE(false, "saveFile", "TKPR_SAVE_FILE"),
	RUN_MODE(false, "run.mode", "TKPR_MODE"),
	// Running config, for single mode
	SINGLE_MODE_HELP(false, "consoleHelp"),
	// default values
	DEFAULT_CONFIG_FILE(true, "default.configFile", CONFIG_FILE),
	DEFAULT_UI_OPTIONS_FILE(true, "default.uiOptionsFile", UI_OPTIONS_FILE),
	DEFAULT_SAVE_FILE(true, "default.saveFile", SAVE_FILE),
	DEFAULT_RUN_MODE(true, "default.runMode", RUN_MODE),
	// static files
	STATIC_GUI_ICON(true, "static.guiIconFile"),
	// other
	GITHUB_REPO_URL(true, "urls.github"),
	GITHUB_DESKTOP_APP_README(true, "urls.desktopReadme"),
	DONATE_URL(true, "urls.donateUrl");
	
	public final boolean readOnly;
	public final String key;
	public final String envVar;
	public final ConfigKeys defaultFor;
	
	ConfigKeys(boolean readOnly, String key) {
		this.readOnly = readOnly;
		this.key = key;
		this.envVar = null;
		this.defaultFor = null;
	}
	
	ConfigKeys(boolean readOnly, String key, String envVar) {
		this.readOnly = readOnly;
		this.key = key;
		this.envVar = envVar;
		this.defaultFor = null;
	}
	
	ConfigKeys(boolean readOnly, String key, ConfigKeys defaultFor) {
		this.readOnly = readOnly;
		this.key = key;
		this.envVar = null;
		this.defaultFor = defaultFor;
	}
	
	public static ConfigKeys getKeyOf(String key) {
		for(ConfigKeys curKey : values()) {
			if(curKey.key.equals(key)) {
				return curKey;
			}
		}
		throw new ConfigKeyDoesNotExistException("Config key does not exist. Erring key: " + key);
	}
	
	public static Collection<ConfigKeys> getKeysWithEnv() {
		List<ConfigKeys> keys = new ArrayList<>();
		for(ConfigKeys key : ConfigKeys.values()) {
			if(key.envVar != null) {
				keys.add(key);
			}
		}
		return keys;
	}
	
	public static Collection<ConfigKeys> getKeysWithDefaultFor() {
		List<ConfigKeys> keys = new ArrayList<>();
		for(ConfigKeys key : ConfigKeys.values()) {
			if(key.defaultFor != null) {
				keys.add(key);
			}
		}
		return keys;
	}
}
