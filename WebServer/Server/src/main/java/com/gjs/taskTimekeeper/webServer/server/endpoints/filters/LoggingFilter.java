package com.gjs.taskTimekeeper.webServer.server.endpoints.filters;

import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.time.Instant;

@Slf4j
@Provider
public class LoggingFilter implements ContainerRequestFilter {
	
	@Context
	UriInfo info;
	
	@Context
	HttpServerRequest request;
	
	@Override
	public void filter(ContainerRequestContext context) {
		final String method = context.getMethod();
		final String path = info.getPath();
		final String address = request.remoteAddress().toString();
		
		context.setProperty("requestId", buildRequestId());
		
		log.info("Request {} - {} {} from IP {}", context.getProperty("requestId"), method, path, address);
	}
	
	private static String buildRequestId() {
		return String.format(
			"%d-%s",
			Instant.now().toEpochMilli(),
			RandomStringUtils.randomAlphanumeric(5)
		);
	}
}
