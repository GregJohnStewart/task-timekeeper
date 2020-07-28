package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception;

public class DataSourceWriteException extends DataSourceException {
	public DataSourceWriteException() {
	}
	
	public DataSourceWriteException(String message) {
		super(message);
	}
	
	public DataSourceWriteException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DataSourceWriteException(Throwable cause) {
		super(cause);
	}
	
	public DataSourceWriteException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
