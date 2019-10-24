package com.gjs.taskTimekeeper.baseCode.managerIO.exception;

/** Exception to associate with errors in TimeManager IO Operations */
public class ManagerIOException extends RuntimeException {
    public ManagerIOException() {}

    public ManagerIOException(String message) {
        super(message);
    }

    public ManagerIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerIOException(Throwable cause) {
        super(cause);
    }

    public ManagerIOException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
