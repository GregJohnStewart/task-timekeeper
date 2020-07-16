package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

public abstract class Anitizer <T> implements Sanitizer<T>, DeSanitizer<T> {
	
	public T anitize(T object, AnitizeOp operation) {
		switch(operation) {
		case SANITIZE:
			return this.sanitize(object);
		case DESANITIZE:
			return this.deSanitize(object);
		}
		throw new IllegalArgumentException("Invalid operation given: " + operation);
	}
}
