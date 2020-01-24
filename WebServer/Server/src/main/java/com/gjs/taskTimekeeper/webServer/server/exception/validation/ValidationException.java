package com.gjs.taskTimekeeper.webServer.server.exception.validation;

import com.gjs.taskTimekeeper.webServer.server.exception.WebServerException;

public class ValidationException extends WebServerException {
    public ValidationException() {
    }

    public ValidationException(String s) {
        super(s);
    }

    public ValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ValidationException(Throwable throwable) {
        super(throwable);
    }

    public ValidationException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
