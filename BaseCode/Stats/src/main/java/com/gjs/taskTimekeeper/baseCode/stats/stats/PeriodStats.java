package com.gjs.taskTimekeeper.baseCode.stats.stats;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodStats {
	private OverallStats overallStats;
	private PercentStats<Task> taskStats;
}
