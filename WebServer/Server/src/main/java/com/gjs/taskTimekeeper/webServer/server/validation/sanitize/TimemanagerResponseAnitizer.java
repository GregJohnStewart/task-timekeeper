package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.TimeManagerResponse;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.action.TimeManagerActionResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;
import java.util.TreeSet;

@ApplicationScoped
public class TimemanagerResponseAnitizer extends Anitizer<TimeManagerResponse> {
	@Inject
	HTMLAnitizer htmlAnitizer;
	
	@Inject
	TimemanagerAnitizer timemanagerAnitizer;
	
	@Inject
	AllStatsAnitizer allStatsSanitizer;
	
	@Override
	public TimeManagerResponse anitize(
		TimeManagerResponse response, AnitizeOp operation
	) {
		if(response == null) {
			return response;
		}
		
		this.timemanagerAnitizer.sanitize(response.getTimeManagerData());
		
		Set<Task> sanitizedTasks = new TreeSet<>();
		if(response.getTimeManagerData() != null) {
			sanitizedTasks.addAll(response.getTimeManagerData().getTasks());
		}
		
		this.allStatsSanitizer.anitize(response.getStats(), operation, sanitizedTasks);
		
		if(response instanceof TimeManagerActionResponse) {
			((TimeManagerActionResponse)response).setRegOut(htmlAnitizer.sanitize(((TimeManagerActionResponse)response).getRegOut()));
			((TimeManagerActionResponse)response).setErrOut(htmlAnitizer.sanitize(((TimeManagerActionResponse)response).getErrOut()));
		}
		
		return response;
	}
}
