package com.gjs.taskTimekeeper.webServer.server.endpoints.filters;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static com.gjs.taskTimekeeper.webServer.server.utils.LoggingUtils.REQUEST_ID_KEY;

@Slf4j
@Provider
public class ResponseLoggingFilter implements ContainerResponseFilter {
	@Override
	public void filter(
		ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext
	) throws IOException {
		log.info(
			"{} - REQUEST COMPLETE, responding with: {}",
			containerRequestContext.getProperty(REQUEST_ID_KEY),
			containerResponseContext.getStatus()
		);
	}
}
