package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TimemanagerSanitizer implements Sanitizer<TimeManager>, DeSanitizer<TimeManager> {
	@Inject
	HTMLSanitizer htmlSanitizer;
	
	@Override
	public TimeManager deSanitize(TimeManager object) {
		//TODO
		return object;
	}
	
	@Override
	public TimeManager sanitize(TimeManager object) {
		//TODO
		return object;
	}
}
