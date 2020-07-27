package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception;

public class DataSourceNotFoundException extends DataSourceException {
	public DataSourceNotFoundException() {
	}
	
	public DataSourceNotFoundException(String message) {
		super(message);
	}
	
	public DataSourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DataSourceNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public DataSourceNotFoundException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
