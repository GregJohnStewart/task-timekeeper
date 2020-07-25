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
public class TimeManagerActionAnitizer extends Anitizer<TimeManagerActionRequest> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeManagerActionAnitizer.class);
	
	@Inject
	ObjectWithStringsAnitizer objectWithStringsAnitizer;
	
	@Override
	public TimeManagerActionRequest anitize(
		TimeManagerActionRequest request, AnitizeOp operation
	) {
		if(request == null) {
			return null;
		}
		this.objectWithStringsAnitizer.anitize(request.getActionConfig(), operation);
		
		return request;
	}
}
