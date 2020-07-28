package com.gjs.taskTimekeeper.baseCode.managerIO.exception;

public class ManagerIOWriteException extends ManagerIOException {
	public ManagerIOWriteException() {
	}
	
	public ManagerIOWriteException(String message) {
		super(message);
	}
	
	public ManagerIOWriteException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ManagerIOWriteException(Throwable cause) {
		super(cause);
	}
	
	public ManagerIOWriteException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
