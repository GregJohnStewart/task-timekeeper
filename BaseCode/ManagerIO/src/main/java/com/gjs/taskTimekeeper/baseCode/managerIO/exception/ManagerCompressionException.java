package com.gjs.taskTimekeeper.baseCode.managerIO.exception;

public class ManagerCompressionException extends ManagerIOException {
	public ManagerCompressionException() {
	}
	
	public ManagerCompressionException(String message) {
		super(message);
	}
	
	public ManagerCompressionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ManagerCompressionException(Throwable cause) {
		super(cause);
	}
	
	public ManagerCompressionException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
