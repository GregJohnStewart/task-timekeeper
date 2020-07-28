package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;

public class DataSourceException extends ManagerIOException {
	public DataSourceException() {
	}
	
	public DataSourceException(String message) {
		super(message);
	}
	
	public DataSourceException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DataSourceException(Throwable cause) {
		super(cause);
	}
	
	public DataSourceException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
