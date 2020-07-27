package com.gjs.taskTimekeeper.desktopApp.config.exception;

public class ConfigKeyDoesNotExistException extends ConfigurationException {
	public ConfigKeyDoesNotExistException() {
	}
	
	public ConfigKeyDoesNotExistException(String message) {
		super(message);
	}
	
	public ConfigKeyDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ConfigKeyDoesNotExistException(Throwable cause) {
		super(cause);
	}
	
	public ConfigKeyDoesNotExistException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
