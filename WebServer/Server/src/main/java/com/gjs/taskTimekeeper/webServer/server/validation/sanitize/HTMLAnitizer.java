package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import org.apache.commons.text.StringEscapeUtils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HTMLAnitizer extends Anitizer<String> {
	@Override
	public String anitize(String object, AnitizeOp operation) {
		switch(operation) {
		case SANITIZE:
			return StringEscapeUtils.escapeHtml4(object);
		case DESANITIZE:
			return StringEscapeUtils.unescapeHtml4(object);
		default:
			throw new IllegalArgumentException("Unexpected anitization operation: " + operation);
		}
	}
}
