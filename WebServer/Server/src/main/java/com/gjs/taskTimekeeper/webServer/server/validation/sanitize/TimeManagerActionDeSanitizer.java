package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.action.TimeManagerActionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * TODO:: test
 */
@ApplicationScoped
public class TimeManagerActionDeSanitizer implements DeSanitizer<TimeManagerActionRequest> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeManagerActionDeSanitizer.class);
	
	@Inject
	ObjectWithStringsAnitizer objectWithStringsAnitizer;
	
	@Override
	public TimeManagerActionRequest deSanitize(TimeManagerActionRequest request) {
		if(request == null) {
			return null;
		}
		this.objectWithStringsAnitizer.deSanitize(request.getActionConfig());
		
		return request;
	}
}
