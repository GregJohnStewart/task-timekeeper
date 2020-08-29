package com.gjs.taskTimekeeper.webServer.server.exception.request.user;

import javax.ws.rs.core.Response;

public class TooManyFailedLoginsException extends UserLockedException {
	public TooManyFailedLoginsException() {
	}
	
	public TooManyFailedLoginsException(String s) {
		super(s);
	}
	
	public TooManyFailedLoginsException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public TooManyFailedLoginsException(Throwable throwable) {
		super(throwable);
	}
	
	public TooManyFailedLoginsException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
	
	public Response toResponse() {
		return this.buildResponse(Response.Status.TOO_MANY_REQUESTS.getStatusCode());
	}
}
