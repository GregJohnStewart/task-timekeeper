package com.gjs.taskTimekeeper.webServer.server.exception.request;

import com.gjs.taskTimekeeper.webServer.server.exception.WebServerException;

import javax.ws.rs.core.Response;

public class RequestException extends WebServerException {
	public RequestException() {
	}
	
	public RequestException(String s) {
		super(s);
	}
	
	public RequestException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public RequestException(Throwable throwable) {
		super(throwable);
	}
	
	public RequestException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
	
	public Response toResponse() {
		return this.buildResponse(Response.Status.BAD_REQUEST.getStatusCode());
	}
}
