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
public class AllStatsAnitizer extends Anitizer<AllStats> {
	@Inject
	HTMLAnitizer htmlAnitizer;
	
	@Inject
	TaskAnitizer taskAnitizer;
	
	public AllStats anitize(AllStats stats, AnitizeOp operation, Set<Task> sanitizedTasks) {
		if(stats == null) {
			return stats;
		}
		
		stats.getPeriodStats().forEach(
			(Integer index, PeriodStats periodStats)->{
				anitizeTaskPercentStats(periodStats.getTaskStats(), sanitizedTasks, operation);
			}
		);
		
		anitizeTaskPercentStats(stats.getOverallTimeSpentOnTasks(), sanitizedTasks, operation);
		
		return stats;
	}
	
	@Override
	public AllStats anitize(AllStats stats, AnitizeOp operation) {
		return anitize(stats, operation, new TreeSet<>());
	}
	
	private void anitizeTaskPercentStats(PercentStats<Task> taskStats, Set<Task> sanitizedTasks, AnitizeOp operation) {
		taskStats.setPercentages(anitizeTaskMap(taskStats.getPercentages(), sanitizedTasks, operation));
		taskStats.setValueStrings(anitizeTaskMap(taskStats.getValueStrings(), sanitizedTasks, operation));
		taskStats.setValues(anitizeTaskMap(taskStats.getValues(), sanitizedTasks, operation));
	}
	
	private <T> Map<Task, T> anitizeTaskMap(Map<Task, T> map, Set<Task> sanitizedTasks, AnitizeOp operation) {
		//noinspection unchecked
		return (Map<Task, T>)map.entrySet().stream().collect(Collectors.toMap(
			entry->{
				if(sanitizedTasks.contains(entry.getKey())) {
					return entry.getKey();
				}
				
				sanitizedTasks.add(entry.getKey());
				
				return taskAnitizer.anitize(entry.getKey(), operation);
			},
			entry->{
				if(entry.getValue() instanceof String) {
					return htmlAnitizer.anitize((String)entry.getValue(), operation);
				} else if(!ClassUtils.isPrimitiveWrapper(entry.getValue().getClass())) {
					//TODO:: ObjectWithStringsAnitize?
				}
				
				return entry.getValue();
			}
		));
	}
}
