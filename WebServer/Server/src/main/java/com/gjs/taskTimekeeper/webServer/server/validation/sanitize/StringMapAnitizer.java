package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class StringMapAnitizer extends Anitizer<Map<String, String>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(StringMapAnitizer.class);
	
	@Inject
	HTMLAnitizer htmlAnitizer;
	
	@Override
	public Map<String, String> anitize(Map<String, String> atts, AnitizeOp operation) {
		if(atts == null) {
			return null;
		}
		return atts.entrySet().stream().collect(Collectors.toMap(
			entry->htmlAnitizer.anitize(entry.getKey(), operation),
			entry->htmlAnitizer.anitize(entry.getValue(), operation)
		));
	}
}
