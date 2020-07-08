package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.TimeManagerResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TimemanagerResponseSanitizer implements Sanitizer<TimeManagerResponse> {
    @Inject
	HTMLAnitizer htmlAnitizer;
	
	@Inject
	TimemanagerAnitizer timemanagerAnitizer;
    
    @Override
    public TimeManagerResponse sanitize(TimeManagerResponse object) {
        //TODO
        return object;
    }
}
