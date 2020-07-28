package com.gjs.taskTimekeeper.webServer.server.exception.request.user;

import com.gjs.taskTimekeeper.webServer.server.exception.request.RequestException;

import javax.ws.rs.core.Response;

public class UserLockedException extends RequestException {
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
	
	public Response toResponse() {
		return this.buildResponse(Response.Status.FORBIDDEN.getStatusCode());
	}
}
