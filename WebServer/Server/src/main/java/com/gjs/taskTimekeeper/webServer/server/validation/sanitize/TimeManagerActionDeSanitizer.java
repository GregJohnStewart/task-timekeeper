package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.action.TimeManagerActionRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TimeManagerActionDeSanitizer implements DeSanitizer<TimeManagerActionRequest> {
	@Inject
	HTMLSanitizer htmlSanitizer;
	
	@Override
	public TimeManagerActionRequest deSanitize(TimeManagerActionRequest object) {
		//TODO
		return object;
	}
}
