package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.TimeManagerResponse;
import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.action.TimeManagerActionResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TimemanagerResponseSanitizer implements Sanitizer<TimeManagerResponse> {
	@Inject
	HTMLAnitizer htmlAnitizer;
	
	@Inject
	TimemanagerAnitizer timemanagerAnitizer;
	
	@Inject
	AllStatsSanitizer allStatsSanitizer;
	
	@Override
	public TimeManagerResponse sanitize(TimeManagerResponse response) {
		this.timemanagerAnitizer.sanitize(response.getTimeManagerData());
		this.allStatsSanitizer.sanitize(response.getStats(), response.getTimeManagerData().getTasks());
		
		if(response instanceof TimeManagerActionResponse) {
			((TimeManagerActionResponse)response).setRegOut(htmlAnitizer.sanitize(((TimeManagerActionResponse)response).getRegOut()));
			((TimeManagerActionResponse)response).setErrOut(htmlAnitizer.sanitize(((TimeManagerActionResponse)response).getErrOut()));
		}
		
		return response;
	}
}
