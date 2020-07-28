package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception;

public class DataSourceReadException extends DataSourceException {
	public DataSourceReadException() {
	}
	
	public DataSourceReadException(String message) {
		super(message);
	}
	
	public DataSourceReadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DataSourceReadException(Throwable cause) {
		super(cause);
	}
	
	public DataSourceReadException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
