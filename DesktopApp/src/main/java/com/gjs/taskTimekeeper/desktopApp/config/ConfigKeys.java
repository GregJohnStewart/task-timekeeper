package com.gjs.taskTimekeeper.desktopApp.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Keys for accessing configuration. */
public enum ConfigKeys {
    // basic config keys, from project.properties
    APP_NAME(false, "app.name"),
    APP_VERSION(false, "app.version"),
    APP_BUILDTIME(false, "app.buildtime"),
    LIB_VERSION(false, "lib.version"),
    // Other config
    CONFIG_FILE(false, "configFile", "TKPR_CONFIG_FILE", true, false),
    UI_OPTIONS_FILE(true, "uiOptionsFile", "TKPR_UI_CONFIG_FILE", true, false),
    SAVE_FILE(true, "saveFile", "TKPR_SAVE_FILE", true, true),
    RUN_MODE(true, "run.mode", "TKPR_MODE"),
    // Running config, for single mode
    SINGLE_MODE_HELP(false, "consoleHelp"),
    // default values
    DEFAULT_CONFIG_FILE(false, "default.configFile", CONFIG_FILE),
    DEFAULT_UI_OPTIONS_FILE(false, "default.uiOptionsFile", UI_OPTIONS_FILE),
    DEFAULT_SAVE_FILE(false, "default.saveFile", SAVE_FILE),
    DEFAULT_RUN_MODE(false, "default.runMode", RUN_MODE),
    // static files
    STATIC_GUI_ICON(false, "static.guiIconFile"),
    // other
    GITHUB_REPO_URL(false, "urls.github"),
    GITHUB_DESKTOP_APP_README(false, "urls.desktopReadme"),
    DONATE_URL(false, "urls.donateUrl");

    public final boolean savable;
    public final String key;
    public final String envVar;
    public final ConfigKeys defaultFor;
    public final boolean isFile;
    public final boolean needsFile;

    ConfigKeys(boolean savable, String key) {
        this.savable = savable;
        this.key = key;
        this.envVar = null;
        this.defaultFor = null;
        this.isFile = false;
        this.needsFile = false;
    }

    ConfigKeys(boolean savable, String key, String envVar) {
        this.savable = savable;
        this.key = key;
        this.envVar = envVar;
        this.defaultFor = null;
        this.isFile = false;
        this.needsFile = false;
    }

    ConfigKeys(boolean savable, String key, String envVar, boolean isFile, boolean needsFile) {
        this.savable = savable;
        this.key = key;
        this.envVar = envVar;
        this.defaultFor = null;
        this.isFile = isFile;
        this.needsFile = needsFile;
    }

    ConfigKeys(boolean savable, String key, ConfigKeys defaultFor) {
        this.savable = savable;
        this.key = key;
        this.envVar = null;
        this.defaultFor = defaultFor;
        this.isFile = false;
        this.needsFile = false;
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

    public static Collection<ConfigKeys> getKeysThatAreFiles() {
        List<ConfigKeys> keys = new ArrayList<>();
        for (ConfigKeys key : ConfigKeys.values()) {
            if (key.isFile) {
                keys.add(key);
            }
        }
        return keys;
    }

    public static Collection<ConfigKeys> getKeysThatAreSavable() {
        List<ConfigKeys> keys = new ArrayList<>();
        for (ConfigKeys key : ConfigKeys.values()) {
            if (key.savable) {
                keys.add(key);
            }
        }
        return keys;
    }
}
