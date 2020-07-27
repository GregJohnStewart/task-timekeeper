package com.gjs.taskTimekeeper.desktopApp.config.exception;

import com.gjs.taskTimekeeper.desktopApp.DesktopAppException;

public class ConfigurationException extends DesktopAppException {
	public ConfigurationException() {
	}
	
	public ConfigurationException(String message) {
		super(message);
	}
	
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ConfigurationException(Throwable cause) {
		super(cause);
	}
	
	public ConfigurationException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
