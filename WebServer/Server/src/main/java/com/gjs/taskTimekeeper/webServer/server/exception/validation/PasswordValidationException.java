package com.gjs.taskTimekeeper.webServer.server.exception.validation;

public class PasswordValidationException extends ValidationException {
	public PasswordValidationException() {
	}
	
	public PasswordValidationException(String s) {
		super(s);
	}
	
	public PasswordValidationException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public PasswordValidationException(Throwable throwable) {
		super(throwable);
	}
	
	public PasswordValidationException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
