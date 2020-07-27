package com.gjs.taskTimekeeper.webServer.server.mappers;

import com.gjs.taskTimekeeper.webServer.server.exception.WebServerException;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class WebServerExceptionMapper implements ExceptionMapper<WebServerException> {
	
	@Override
	public Response toResponse(WebServerException exception) {
		log.warn("Handling a server exception: ", exception);
		return exception.toResponse();
	}
}
