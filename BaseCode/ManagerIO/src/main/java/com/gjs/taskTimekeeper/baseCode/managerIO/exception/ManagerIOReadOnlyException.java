package com.gjs.taskTimekeeper.baseCode.managerIO.exception;

public class ManagerIOReadOnlyException extends ManagerIOWriteException {
	public ManagerIOReadOnlyException() {
	}
	
	public ManagerIOReadOnlyException(String message) {
		super(message);
	}
	
	public ManagerIOReadOnlyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ManagerIOReadOnlyException(Throwable cause) {
		super(cause);
	}
	
	public ManagerIOReadOnlyException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
