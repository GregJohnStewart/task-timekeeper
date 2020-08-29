package com.gjs.taskTimekeeper.webServer.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.spi.HttpRequest;
import org.slf4j.Logger;

@Slf4j
public class LoggingUtils {
	
	public static final String REQUEST_ID_KEY = "requestId";
	
	public static void endpointInfoLog(
		Logger logger,
		HttpRequest context,//doesn't work, can't be passed to endpoints
		String format,
		Object... params
	) {
		
		logger.info(
			context.getAttribute(REQUEST_ID_KEY) + " - " + format,
			params
		);
	}
	
	public static void endpointDebugLog(
		Logger logger,
		HttpRequest context,//doesn't work, can't be passed to endpoints
		String format,
		Object... params
	) {
		
		logger.debug(
			context.getAttribute(REQUEST_ID_KEY) + " - " + format,
			params
		);
	}
	
	public static void endpointTraceLog(
		Logger logger,
		HttpRequest context,//doesn't work, can't be passed to endpoints
		String format,
		Object... params
	) {
		
		logger.trace(
			context.getAttribute(REQUEST_ID_KEY) + " - " + format,
			params
		);
	}
	
	public static void endpointWarnLog(
		Logger logger,
		HttpRequest context,//doesn't work, can't be passed to endpoints
		String format,
		Object... params
	) {
		logger.warn(
			context.getAttribute(REQUEST_ID_KEY) + " - " + format,
			params
		);
	}
	
	public static void endpointErrorLog(
		Logger logger,
		HttpRequest context,//doesn't work, can't be passed to endpoints
		String format,
		Object... params
	) {
		logger.error(
			context.getAttribute(REQUEST_ID_KEY) + " - " + format,
			params
		);
	}
}
