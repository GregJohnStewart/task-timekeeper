package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

public interface DeSanitizer <T> {
	public T deSanitize(T object);
}
