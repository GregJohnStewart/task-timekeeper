package com.gjs.taskTimekeeper.webServer.server.exception.request.user;

public class UserLockedException extends UserLoginException {
	public UserLockedException() {
	}
	
	public UserLockedException(String s) {
		super(s);
	}
	
	public UserLockedException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public UserLockedException(Throwable throwable) {
		super(throwable);
	}
	
	public UserLockedException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
