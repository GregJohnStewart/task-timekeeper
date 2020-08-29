package com.gjs.taskTimekeeper.webServer.server.exception.request.user;

import javax.ws.rs.core.Response;

public class UserLoginException extends UserRequestException {
	public UserLoginException() {
	}
	
	public UserLoginException(String s) {
		super(s);
	}
	
	public UserLoginException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public UserLoginException(Throwable throwable) {
		super(throwable);
	}
	
	public UserLoginException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
	
	public Response toResponse() {
		return this.buildResponse(Response.Status.FORBIDDEN.getStatusCode());
	}
}
