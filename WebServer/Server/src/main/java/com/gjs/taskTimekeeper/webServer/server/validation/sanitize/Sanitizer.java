package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

public interface Sanitizer <T> {
	public T sanitize(T object);
}
