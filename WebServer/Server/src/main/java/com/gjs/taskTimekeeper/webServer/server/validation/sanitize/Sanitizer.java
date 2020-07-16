package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

public interface Sanitizer <T> {
	
	/**
	 * Sanitizes the object given.
	 * <p>
	 * If the object is immutable, returns a new object with sanitized values.
	 * <p>
	 * If the object is mutable, makes the changes in place, returns the same object given.
	 *
	 * @param object the object to sanitize
	 * @return The sanitized object
	 */
	public T sanitize(T object);
}
