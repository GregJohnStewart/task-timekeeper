package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class TimemanagerAnitizer extends Anitizer<TimeManager> {
	@Inject
	HTMLAnitizer htmlAnitizer;
	
	private Map<String, String> anitizeAtts(Map<String, String> atts, AnitizeOp op) {
		if(atts == null) {
			return null;
		}
		return atts.entrySet().stream().collect(Collectors.toMap(
			entry->htmlAnitizer.anitize(entry.getKey(), op),
			entry->htmlAnitizer.anitize(entry.getValue(), op)
		));
	}
	
	@Override
	public TimeManager anitize(
		TimeManager manager,
		AnitizeOp op
	) {
		if(manager == null) {
			return null;
		}
		
		for(Task task : manager.getTasks()) {
			task.setName(new Name(this.htmlAnitizer.anitize(task.getName().getName(), op)));
			task.setAttributes(this.anitizeAtts(task.getAttributes(), op));
		}
		
		for(WorkPeriod period : manager.getWorkPeriods()) {
			period.setAttributes(
				this.anitizeAtts(period.getAttributes(), op)
			);
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
