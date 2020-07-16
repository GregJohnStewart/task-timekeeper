package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

public interface DeSanitizer <T> {
	/**
	 * De sanitizes the object given.
	 * <p>
	 * If the object is immutable, returns a new object with desanitized values.
	 * <p>
	 * If the object is mutable, makes the changes in place, returns the same object given.
	 *
	 * @param object the object to de sanitize
	 * @return The desanitized object
	 */
	public T deSanitize(T object);
}
