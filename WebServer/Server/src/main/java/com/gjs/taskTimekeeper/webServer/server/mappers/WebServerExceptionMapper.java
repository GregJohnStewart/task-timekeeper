package com.gjs.taskTimekeeper.webServer.server.mappers;

import com.gjs.taskTimekeeper.webServer.server.exception.WebServerException;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static com.gjs.taskTimekeeper.webServer.server.utils.LoggingUtils.REQUEST_ID_KEY;

@Provider
@Slf4j
public class WebServerExceptionMapper implements ExceptionMapper<WebServerException> {
	@Context
	HttpRequest request;
	
	@Override
	public Response toResponse(WebServerException exception) {
		log.warn(
			"{} - Handling an exception: ",
			request.getAttribute(REQUEST_ID_KEY),
			exception
		);
		return exception.toResponse();
	}
}
