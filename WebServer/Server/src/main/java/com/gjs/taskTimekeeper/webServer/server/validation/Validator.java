package com.gjs.taskTimekeeper.webServer.server.validation;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.ValidationException;

public abstract class Validator <T> {

    public T validateAndSanitize(T object) throws ValidationException {
        T sanitized = this.sanitize(object);
        this.validate(sanitized);
        return sanitized;
    }

    protected abstract T sanitize(T object);

    protected abstract void validate(T object) throws ValidationException;
}
