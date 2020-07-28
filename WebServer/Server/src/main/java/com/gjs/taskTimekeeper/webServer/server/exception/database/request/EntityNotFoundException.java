package com.gjs.taskTimekeeper.webServer.server.exception.database.request;

import javax.ws.rs.core.Response;

public class EntityNotFoundException extends DatabaseRequestException {
	public EntityNotFoundException() {
	}
	
	public EntityNotFoundException(String s) {
		super(s);
	}
	
	public EntityNotFoundException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public EntityNotFoundException(Throwable throwable) {
		super(throwable);
	}
	
	public EntityNotFoundException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
	
	public Response toResponse() {
		return this.buildResponse(Response.Status.NOT_FOUND.getStatusCode());
	}
}
