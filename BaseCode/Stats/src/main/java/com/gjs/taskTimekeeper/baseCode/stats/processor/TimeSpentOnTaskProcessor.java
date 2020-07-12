package com.gjs.taskTimekeeper.baseCode.stats.processor;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;
import com.gjs.taskTimekeeper.baseCode.stats.stats.PercentStats;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class TimeSpentOnTaskProcessor extends StatProcessor<PercentStats<Task>> {
	private static final TimeSpentOnTaskProcessor INSTANCE = new TimeSpentOnTaskProcessor();
	
	public static TimeSpentOnTaskProcessor getInstance() {
		return INSTANCE;
	}
	
	@Override
	public PercentStats<Task> process(TimeManager manager) {
		// more efficient to make map and then create results object
		Map<Task, Number> resultMap = new HashMap<>();
		Map<Task, String> stringMap = new HashMap<>();
		
		for(Task task : manager.getTasks()) {
			Duration duration = Duration.ZERO;
			for(WorkPeriod period : manager.getWorkPeriodsWith(task)) {
				duration = duration.plus(period.getTotalTimeWith(task.getName()));
			}
			resultMap.put(task, duration.getSeconds());
			stringMap.put(task, TimeParser.toDurationString(duration));
		}
		
		return this.setResults(new PercentStats<>(resultMap, stringMap));
	}
	
	public PercentStats<Task> process(TimeManager manager, WorkPeriod period)
		throws StatProcessingException {
		if(!manager.getWorkPeriods().contains(period)) {
			throw new StatProcessingException("Work period not in manager given.");
		}
		// more efficient to make map and then create results object
		Map<Task, Number> resultMap = new HashMap<>();
		
		for(Name taskName : period.getTaskNames()) {
			Task task = manager.getTaskByName(taskName);
			resultMap.put(task, period.getTotalTimeWith(task.getName()).getSeconds());
		}
		
		return this.setResults(new PercentStats<>(resultMap));
	}
}
