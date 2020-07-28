package com.gjs.taskTimekeeper.webServer.server.exception.request.user;

public class CorruptedKeyException extends UserRequestException {
	public CorruptedKeyException() {
	}
	
	public CorruptedKeyException(String s) {
		super(s);
	}
	
	public CorruptedKeyException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public CorruptedKeyException(Throwable throwable) {
		super(throwable);
	}
	
	public CorruptedKeyException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
