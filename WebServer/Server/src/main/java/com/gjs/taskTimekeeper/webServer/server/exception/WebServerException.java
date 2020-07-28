package com.gjs.taskTimekeeper.webServer.server.exception;

import javax.ws.rs.core.Response;

public class WebServerException extends RuntimeException {
	public WebServerException() {
	}
	
	public WebServerException(String s) {
		super(s);
	}
	
	public WebServerException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public WebServerException(Throwable throwable) {
		super(throwable);
	}
	
	public WebServerException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
	
	protected Response buildResponse(int code) {
		return Response.status(code).entity(this.getMessage()).build();
	}
	
	public Response toResponse() {
		return this.buildResponse(
			Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
		);
	}
}
