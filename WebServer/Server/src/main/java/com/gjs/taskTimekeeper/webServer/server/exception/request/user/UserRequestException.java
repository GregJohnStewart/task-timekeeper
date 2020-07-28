package com.gjs.taskTimekeeper.webServer.server.exception.request.user;

import com.gjs.taskTimekeeper.webServer.server.exception.request.RequestException;

public class UserRequestException extends RequestException {
	public UserRequestException() {
	}
	
	public UserRequestException(String s) {
		super(s);
	}
	
	public UserRequestException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public UserRequestException(Throwable throwable) {
		super(throwable);
	}
	
	public UserRequestException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
