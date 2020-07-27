package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.action.TimeManagerActionRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * TODO:: test
 */
@ApplicationScoped
public class TimeManagerActionAnitizer extends Anitizer<TimeManagerActionRequest> {
	
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
