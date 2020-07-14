package com.gjs.taskTimekeeper.baseCode.stats.processor;


import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.stats.stats.AllStats;
import com.gjs.taskTimekeeper.baseCode.stats.stats.PeriodStats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

/**
 * Processor for processing all stats about a manager.
 * <p>
 * TODO:: test
 */
public class AllStatsProcessor extends StatProcessor<AllStats> {
	private final OverallStatProcessor overallStatProcessor = new OverallStatProcessor();
	private final TimeSpentOnTaskProcessor timeSpentOnTaskProcessor = new TimeSpentOnTaskProcessor();
	
	@Override
	public AllStats process(TimeManager manager) throws StatProcessingException {
		AllStats output = new AllStats();
		
		output.setOverallStats(this.overallStatProcessor.process(manager));
		output.setOverallTimeSpentOnTasks(this.timeSpentOnTaskProcessor.process(manager));
		
		SortedSet<WorkPeriod> workPeriods = manager.getWorkPeriods();
		
		int i = workPeriods.size();
		Iterator<WorkPeriod> workPeriodIterator = workPeriods.iterator();
		
		Map<Integer, PeriodStats> periodStatsMap = new HashMap<>();
		while(workPeriodIterator.hasNext()) {
			WorkPeriod curPeriod = workPeriodIterator.next();
			
			PeriodStats periodStats = new PeriodStats(
				overallStatProcessor.process(manager, curPeriod),
				timeSpentOnTaskProcessor.process(manager, curPeriod)
			);
			
			periodStatsMap.put(i, periodStats);
			
			i--;
		}
		
		output.setPeriodStats(periodStatsMap);
		
		return output;
	}
}
