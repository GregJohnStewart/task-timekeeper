package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.stats.stats.AllStats;
import com.gjs.taskTimekeeper.baseCode.stats.stats.PercentStats;
import com.gjs.taskTimekeeper.baseCode.stats.stats.PeriodStats;
import org.apache.commons.lang3.ClassUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@ApplicationScoped
public class AllStatsSanitizer implements Sanitizer<AllStats> {
	@Inject
	HTMLAnitizer htmlAnitizer;
	
	@Inject
	TaskAnitizer taskAnitizer;
	
	public AllStats sanitize(AllStats stats, Set<Task> sanitizedTasks) {
		if(stats == null) {
			return stats;
		}
		
		stats.getPeriodStats().forEach(
			(Integer index, PeriodStats periodStats)->{
				sanitizeTaskPercentStats(periodStats.getTaskStats(), sanitizedTasks);
			}
		);
		
		sanitizeTaskPercentStats(stats.getOverallTimeSpentOnTasks(), sanitizedTasks);
		
		return stats;
	}
	
	@Override
	public AllStats sanitize(AllStats stats) {
		return sanitize(stats, new TreeSet<>());
	}
	
	private void sanitizeTaskPercentStats(PercentStats<Task> taskStats, Set<Task> sanitizedTasks) {
		taskStats.setPercentages(sanitizeTaskMap(taskStats.getPercentages(), sanitizedTasks));
		taskStats.setValueStrings(sanitizeTaskMap(taskStats.getValueStrings(), sanitizedTasks));
		taskStats.setValues(sanitizeTaskMap(taskStats.getValues(), sanitizedTasks));
	}
	
	private <T> Map<Task, T> sanitizeTaskMap(Map<Task, T> map, Set<Task> sanitizedTasks) {
		//noinspection unchecked
		return (Map<Task, T>)map.entrySet().stream().collect(Collectors.toMap(
			entry->{
				if(sanitizedTasks.contains(entry.getKey())) {
					return entry.getKey();
				}
				
				sanitizedTasks.add(entry.getKey());
				
				return taskAnitizer.sanitize(entry.getKey());
			},
			entry->{
				if(entry.getValue() instanceof String) {
					return htmlAnitizer.sanitize((String)entry.getValue());
				} else if(!ClassUtils.isPrimitiveWrapper(entry.getValue().getClass())) {
				
				}
				
				return entry.getValue();
			}
		));
	}
}
