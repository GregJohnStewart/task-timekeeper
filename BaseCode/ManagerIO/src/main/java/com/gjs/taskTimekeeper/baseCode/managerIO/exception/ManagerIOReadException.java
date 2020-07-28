package com.gjs.taskTimekeeper.baseCode.managerIO.exception;

public class ManagerIOReadException extends ManagerIOException {
	public ManagerIOReadException() {
	}
	
	public ManagerIOReadException(String message) {
		super(message);
	}
	
	public ManagerIOReadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ManagerIOReadException(Throwable cause) {
		super(cause);
	}
	
	public ManagerIOReadException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
