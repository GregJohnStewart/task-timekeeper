package com.gjs.taskTimekeeper.desktopApp;

public class DesktopAppException extends RuntimeException {
	public DesktopAppException() {
	}
	
	public DesktopAppException(String message) {
		super(message);
	}
	
	public DesktopAppException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DesktopAppException(Throwable cause) {
		super(cause);
	}
	
	public DesktopAppException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
