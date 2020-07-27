package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception;

public class DataSourceCredentialException extends DataSourceConnectionException {
	public DataSourceCredentialException() {
	}
	
	public DataSourceCredentialException(String message) {
		super(message);
	}
	
	public DataSourceCredentialException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DataSourceCredentialException(Throwable cause) {
		super(cause);
	}
	
	public DataSourceCredentialException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
