package com.gjs.taskTimekeeper.baseCode.stats.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class OverallStats extends Stats {
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	/**
	 * Total time recorded in the time manager or period.
	 */
	private Duration totalTime;
	/**
	 * The number of periods held. -1 if results about a work period
	 */
	private int numPeriods;
	/**
	 * The number of tasks used in the manager or period
	 */
	private int numTasksUsed;
	/**
	 * The number of total tasks held. -1 if about a work period.
	 */
	private int numTasksTotal;
	/**
	 * Number of spans
	 */
	private int numSpans;
	/**
	 * Average number of spans per period. -1 if about a work period.
	 */
	private double numSpansPerPeriodAverage;
	/**
	 * If all tasks held are complete
	 */
	private boolean allComplete;
	/**
	 * The average length of time for spans.
	 */
	private Duration averageSpanLength;
	/**
	 * The average length of time fot work periods. Null if about a work period
	 */
	private Duration averageWorkPeriodLength;
	
	// TODO:: test
	public OverallStats(
		LocalDateTime startDateTime,
		LocalDateTime endDateTime,
		Duration totalTime,
		int numTasksUsed,
		int numSpans,
		boolean allComplete,
		Duration averageSpanLength
	) {
		this(
			startDateTime,
			endDateTime,
			totalTime,
			-1,
			numTasksUsed,
			-1,
			numSpans,
			-1,
			allComplete,
			averageSpanLength,
			null
		);
	}
	
	// TODO:: test
	public static OverallStatsBuilder builderForWorkPeriod() {
		OverallStatsBuilder builder = builder();
		
		builder.numPeriods(-1);
		builder.numTasksTotal(-1);
		builder.averageWorkPeriodLength(null);
		
		return builder;
	}
}
