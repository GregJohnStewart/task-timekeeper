package com.gjs.taskTimekeeper.baseCode.stats.processor;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;
import com.gjs.taskTimekeeper.baseCode.stats.stats.OverallStats;
import com.gjs.taskTimekeeper.baseCode.stats.stats.OverallStats.OverallStatsBuilder;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.TreeSet;

/**
 * Stat processor for overall stats on a time manager. TODO:: test
 */
public class OverallStatProcessor extends StatProcessor<OverallStats> {
	
	@Override
	public OverallStats process(TimeManager manager) throws StatProcessingException {
		OverallStatsBuilder builder = OverallStats.builder();
		
		//TODO:: start, end datetimes
		
		builder.numTasksTotal(manager.getTasks().size());
		builder.numPeriods(manager.getWorkPeriods().size());
		
		boolean complete = true;
		Duration total = Duration.ZERO;
		Set<Name> tasksUsed = new TreeSet<>();
		int spanCount = 0;
		long numSecsInAllTasks = 0;
		for(WorkPeriod period : manager.getWorkPeriods()) {
			if(complete && period.isUnCompleted()) {
				complete = false;
			}
			
			spanCount += period.getNumTimespans();
			
			Duration totalPeriodTime = period.getTotalTime();
			
			total = total.plus(totalPeriodTime);
			tasksUsed.addAll(period.getTaskNames());
			
			numSecsInAllTasks += totalPeriodTime.getSeconds();
		}
		builder.allComplete(complete);
		builder.totalTime(total);
		builder.numTasksUsed(tasksUsed.size());
		builder.numSpans(spanCount);
		
		if(!manager.getWorkPeriods().isEmpty()) {
			builder.numSpansPerPeriodAverage(
				(double)spanCount / (double)manager.getWorkPeriods().size());
			builder.averageWorkPeriodLength(
				Duration.of(
					numSecsInAllTasks / manager.getWorkPeriods().size(),
					ChronoUnit.SECONDS
				));
		} else {
			builder.numSpansPerPeriodAverage(0);
			builder.averageWorkPeriodLength(Duration.ZERO);
		}
		
		if(spanCount != 0) {
			builder.averageSpanLength(
				Duration.of(numSecsInAllTasks / spanCount, ChronoUnit.SECONDS));
		} else {
			builder.averageSpanLength(Duration.ZERO);
		}
		
		return builder.build();
	}
	
	// TODO:: test
	public OverallStats process(TimeManager manager, WorkPeriod period)
		throws StatProcessingException {
		if(!manager.getWorkPeriods().contains(period)) {
			throw new StatProcessingException("Work period not in manager given.");
		}
		OverallStatsBuilder builder = OverallStats.builderForWorkPeriod();
		
		builder.startDateTime(period.getStart());
		builder.endDateTime(period.getEnd());
		builder.numTasksUsed(period.getTaskNames().size());
		builder.allComplete(!period.isUnCompleted());
		Duration totalTime = period.getTotalTime();
		builder.totalTime(totalTime);
		builder.numSpans(period.getNumTimespans());
		
		if(period.getNumTimespans() != 0) {
			builder.averageSpanLength(
				Duration.of(
					totalTime.getSeconds() / period.getNumTimespans(), ChronoUnit.SECONDS));
		} else {
			builder.averageSpanLength(Duration.ZERO);
		}
		
		return builder.build();
	}
}
