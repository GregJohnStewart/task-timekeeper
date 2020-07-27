package com.gjs.taskTimekeeper.webServer.server.exception.request;

import javax.ws.rs.core.Response;

public class TooManyRequestsException extends RequestException {
	
	public TooManyRequestsException() {
	}
	
	public TooManyRequestsException(String s) {
		super(s);
	}
	
	public TooManyRequestsException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public TooManyRequestsException(Throwable throwable) {
		super(throwable);
	}
	
	public TooManyRequestsException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
	
	public Response toResponse() {
		return this.buildResponse(Response.Status.TOO_MANY_REQUESTS.getStatusCode());
	}
}
