package com.gjs.taskTimekeeper.webServer.server.exception.request.user;

import com.gjs.taskTimekeeper.webServer.server.exception.request.RequestException;

import javax.ws.rs.core.Response;

public class UserUnauthorizedException extends RequestException {
	public UserUnauthorizedException() {
		super();
	}
	
	public UserUnauthorizedException(String s) {
		super(s);
	}
	
	public UserUnauthorizedException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public UserUnauthorizedException(Throwable throwable) {
		super(throwable);
	}
	
	public UserUnauthorizedException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
	
	@Override
	public Response toResponse() {
		return this.buildResponse(Response.Status.UNAUTHORIZED.getStatusCode());
	}
}
