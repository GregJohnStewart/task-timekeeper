package com.gjs.taskTimekeeper.webServer.server.exception.request.user;

public class UserRegistrationException extends UserRequestException {
	public UserRegistrationException() {
	}
	
	public UserRegistrationException(String s) {
		super(s);
	}
	
	public UserRegistrationException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public UserRegistrationException(Throwable throwable) {
		super(throwable);
	}
	
	public UserRegistrationException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
