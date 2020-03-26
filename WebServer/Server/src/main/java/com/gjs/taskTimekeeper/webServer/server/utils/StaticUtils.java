package com.gjs.taskTimekeeper.webServer.server.utils;

public class StaticUtils {

    /**
     * @return the current time in seconds since epoch
     */
    public static long currentTimeInSecs() {
        long currentTimeMS = System.currentTimeMillis();
        return (currentTimeMS / 1000L);
    }
}
