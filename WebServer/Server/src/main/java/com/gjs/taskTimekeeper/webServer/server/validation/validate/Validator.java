package com.gjs.taskTimekeeper.webServer.server.validation.validate;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.ValidationException;
import com.gjs.taskTimekeeper.webServer.server.validation.sanitize.Sanitizer;

public abstract class Validator <T> implements Sanitizer<T> {
	
	public T validateAndSanitize(T object) throws ValidationException {
		T sanitized = this.sanitize(object);
		this.validate(sanitized);
		return sanitized;
	}
	
	protected abstract void validate(T object) throws ValidationException;
}
