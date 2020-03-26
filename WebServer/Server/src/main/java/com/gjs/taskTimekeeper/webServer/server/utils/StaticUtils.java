package com.gjs.taskTimekeeper.webServer.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class StaticUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticUtils.class);

    /**
     * @return the current time in seconds since epoch
     */
    public static long currentTimeInSecs() {
        long currentTimeMS = System.currentTimeMillis();
        return (currentTimeMS / 1000L);
    }

    /**
     * Gets
     * @param resourceLocation
     * @return
     * @throws MalformedURLException
     */
    public static URL resourceAsUrl(String resourceLocation) throws MalformedURLException {
        URL url = StaticUtils.class.getClassLoader().getResource(resourceLocation);
        if(url == null){
            LOGGER.debug("Resource not in classpath.");
            url = new URL("file:" + resourceLocation);
        }else{
            LOGGER.debug("Resource in classpath.");
        }
        return url;
    }
}
