package com.gjs.taskTimekeeper.webServer.server.exception.validation;

public class EmailValidationException extends ValidationException {
	public EmailValidationException() {
	}
	
	public EmailValidationException(String s) {
		super(s);
	}
	
	public EmailValidationException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public EmailValidationException(Throwable throwable) {
		super(throwable);
	}
	
	public EmailValidationException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
