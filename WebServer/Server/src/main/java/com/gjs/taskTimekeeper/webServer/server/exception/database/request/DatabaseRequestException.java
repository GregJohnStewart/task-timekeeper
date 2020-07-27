package com.gjs.taskTimekeeper.webServer.server.exception.database.request;

import com.gjs.taskTimekeeper.webServer.server.exception.database.DatabaseException;

public class DatabaseRequestException extends DatabaseException {
	public DatabaseRequestException() {
	}
	
	public DatabaseRequestException(String s) {
		super(s);
	}
	
	public DatabaseRequestException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public DatabaseRequestException(Throwable throwable) {
		super(throwable);
	}
	
	public DatabaseRequestException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
