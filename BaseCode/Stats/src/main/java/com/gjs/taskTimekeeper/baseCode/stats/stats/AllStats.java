package com.gjs.taskTimekeeper.baseCode.stats.stats;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllStats extends Stats {
	public OverallStats overallStats;
	public Map<Integer, OverallStats> periodStats;
	public PercentStats<Task> overallTimeSpentOnTasks;
	public LinkedHashMap<WorkPeriod, PercentStats<Task>> workPeriodTimeSpentOnTasks;
}
