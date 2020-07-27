package com.gjs.taskTimekeeper.baseCode.core;

public class TimeKeeperException extends RuntimeException {
	public TimeKeeperException() {
	}
	
	public TimeKeeperException(String message) {
		super(message);
	}
	
	public TimeKeeperException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TimeKeeperException(Throwable cause) {
		super(cause);
	}
	
	public TimeKeeperException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
