package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception;

public class DataSourceReadOnlyException extends DataSourceException {
	public DataSourceReadOnlyException() {
	}
	
	public DataSourceReadOnlyException(String message) {
		super(message);
	}
	
	public DataSourceReadOnlyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DataSourceReadOnlyException(Throwable cause) {
		super(cause);
	}
	
	public DataSourceReadOnlyException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
