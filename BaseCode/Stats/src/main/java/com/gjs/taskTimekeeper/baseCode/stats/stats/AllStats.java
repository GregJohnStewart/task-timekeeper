package com.gjs.taskTimekeeper.baseCode.stats.stats;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllStats extends Stats {
	/**
	 * The overall stats for the time manager
	 */
	public OverallStats overallStats;
	/**
	 * Stats for individual work periods.
	 */
	public Map<Integer, PeriodStats> periodStats;
	/**
	 * Stats for tasks themselves
	 */
	public PercentStats<Task> overallTimeSpentOnTasks;
}
