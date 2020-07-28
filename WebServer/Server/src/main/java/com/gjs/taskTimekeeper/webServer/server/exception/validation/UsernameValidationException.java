package com.gjs.taskTimekeeper.webServer.server.exception.validation;

public class UsernameValidationException extends ValidationException {
	public UsernameValidationException() {
	}
	
	public UsernameValidationException(String s) {
		super(s);
	}
	
	public UsernameValidationException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public UsernameValidationException(Throwable throwable) {
		super(throwable);
	}
	
	public UsernameValidationException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
