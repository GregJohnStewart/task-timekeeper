package com.gjs.taskTimekeeper.webServer.server.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public final class StaticUtils {
	
	/**
	 * @return the current time in seconds since epoch
	 */
	public static long currentTimeInSecs() {
		long currentTimeMS = System.currentTimeMillis();
		return (currentTimeMS / 1000L);
	}
	
	/**
	 * Gets the full URL for a file, either on the classpath or filesystem.
	 *
	 * @param resourceLocation The location of the resource to get.
	 * @return The full url of the resource.
	 * @throws MalformedURLException If the resource location was invalid for a URL
	 */
	public static URL resourceAsUrl(String resourceLocation) throws MalformedURLException {
		URL url = StaticUtils.class.getClassLoader().getResource(resourceLocation);
		if(url == null) {
			log.debug("Resource not in classpath.");
			url = new URL("file:" + resourceLocation);
		} else {
			log.debug("Resource in classpath.");
		}
		return url;
	}
}
