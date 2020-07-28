package com.gjs.taskTimekeeper.webServer.server.exception.database;

import com.gjs.taskTimekeeper.webServer.server.exception.WebServerException;

public class DatabaseException extends WebServerException {
	public DatabaseException() {
	}
	
	public DatabaseException(String s) {
		super(s);
	}
	
	public DatabaseException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public DatabaseException(Throwable throwable) {
		super(throwable);
	}
	
	public DatabaseException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
