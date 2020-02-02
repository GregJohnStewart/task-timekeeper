package com.gjs.taskTimekeeper.webServer.server.exception;

import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.Error;

import javax.ws.rs.core.Response;

public class WebServerException extends RuntimeException {
    public WebServerException() {
    }

    public WebServerException(String s) {
        super(s);
    }

    public WebServerException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public WebServerException(Throwable throwable) {
        super(throwable);
    }

    public WebServerException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }

    public Response toResponse(){
        return Response.serverError().entity(
                new Error(
                        this.getMessage(),
                        Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
                )
        ).build();
    }
}
