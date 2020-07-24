package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.Timespan;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TimemanagerAnitizer extends Anitizer<TimeManager> {
	@Inject
	TaskAnitizer taskAnitizer;
	
	@Inject
	StringMapAnitizer stringMapAnitizer;
	
	@Inject
	HTMLAnitizer htmlAnitizer;
	
	@Override
	public TimeManager anitize(
		TimeManager manager,
		AnitizeOp op
	) {
		if(manager == null) {
			return null;
		}
		
		for(Task task : manager.getTasks()) {
			taskAnitizer.anitize(task, op);
		}
		
		for(WorkPeriod period : manager.getWorkPeriods()) {
			period.setAttributes(
				this.stringMapAnitizer.anitize(period.getAttributes(), op)
			);
			
			for(Timespan timespan : period.getTimespans()) {
				timespan.setTaskName(new Name(htmlAnitizer.anitize(timespan.getTaskName().getName(), op)));
			}
		}
		
		return manager;
	}
	
	@Override
	public TimeManager deSanitize(TimeManager object) {
		return this.anitize(object, AnitizeOp.DESANITIZE);
	}
	
	@Override
	public TimeManager sanitize(TimeManager object) {
		return this.anitize(object, AnitizeOp.SANITIZE);
	}
}
