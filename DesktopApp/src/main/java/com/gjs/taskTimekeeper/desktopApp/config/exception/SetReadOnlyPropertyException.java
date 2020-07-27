package com.gjs.taskTimekeeper.desktopApp.config.exception;

public class SetReadOnlyPropertyException extends ConfigurationException {
	public SetReadOnlyPropertyException() {
	}
	
	public SetReadOnlyPropertyException(String message) {
		super(message);
	}
	
	public SetReadOnlyPropertyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SetReadOnlyPropertyException(Throwable cause) {
		super(cause);
	}
	
	public SetReadOnlyPropertyException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
