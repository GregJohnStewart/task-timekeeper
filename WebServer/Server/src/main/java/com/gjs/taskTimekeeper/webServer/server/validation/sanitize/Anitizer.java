package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

public abstract class Anitizer <T> implements Sanitizer<T>, DeSanitizer<T> {
	
	public abstract T anitize(T object, AnitizeOp operation);
	
	@Override
	public T deSanitize(T object) {
		return this.anitize(object, AnitizeOp.DESANITIZE);
	}
	
	@Override
	public T sanitize(T object) {
		return this.anitize(object, AnitizeOp.SANITIZE);
	}
}
