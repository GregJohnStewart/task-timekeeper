package com.gjs.taskTimekeeper.webServer.server.validation;

public abstract class Validator <T> {

    public abstract T validateAndSanitize(T object);

    protected abstract T sanitize(T object);

    protected abstract void validate(T object);
}
