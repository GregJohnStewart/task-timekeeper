package com.gjs.taskTimekeeper.webServer.server.exception.request.user;

public class IncorrectPasswordException extends UserRequestException {
    public IncorrectPasswordException() {
    }

    public IncorrectPasswordException(String s) {
        super(s);
    }

    public IncorrectPasswordException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public IncorrectPasswordException(Throwable throwable) {
        super(throwable);
    }

    public IncorrectPasswordException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
