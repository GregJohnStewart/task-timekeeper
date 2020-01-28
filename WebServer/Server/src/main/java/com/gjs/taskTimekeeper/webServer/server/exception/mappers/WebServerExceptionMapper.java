package com.gjs.taskTimekeeper.webServer.server.exception.mappers;

import com.gjs.taskTimekeeper.webServer.server.exception.WebServerException;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebServerExceptionMapper implements ExceptionMapper<WebServerException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebServerExceptionMapper.class);

    @Override
    public Response toResponse(WebServerException exception) {
        LOGGER.warn("Caught miscellaneous exception: ", exception);
        return Response.serverError().entity(new Error(exception.getMessage(), 500)).build();
    }
}
