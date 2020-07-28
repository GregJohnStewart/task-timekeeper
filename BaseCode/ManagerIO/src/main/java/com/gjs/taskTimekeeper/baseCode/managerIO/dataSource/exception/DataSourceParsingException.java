package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception;

public class DataSourceParsingException extends DataSourceException {
	public DataSourceParsingException() {
	}
	
	public DataSourceParsingException(String message) {
		super(message);
	}
	
	public DataSourceParsingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DataSourceParsingException(Throwable cause) {
		super(cause);
	}
	
	public DataSourceParsingException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
