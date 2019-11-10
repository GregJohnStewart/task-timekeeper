package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;

public abstract class ModeRunner {
    protected final DesktopAppConfiguration config;

    public ModeRunner(DesktopAppConfiguration config) {
        this.config = config;
    }

    public abstract void run();
}
