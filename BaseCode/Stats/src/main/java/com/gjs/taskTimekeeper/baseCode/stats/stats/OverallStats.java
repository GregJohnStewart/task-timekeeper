package com.gjs.taskTimekeeper.baseCode.stats.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class OverallStats extends Stats {
	private final LocalDateTime startDateTime;
	private final LocalDateTime endDateTime;
	/**
	 * Total time recorded in the time manager or period.
	 */
	private final Duration totalTime;
	/**
	 * The number of periods held. -1 if results about a work period
	 */
	private final int numPeriods;
	/**
	 * The number of tasks used in the manager or period
	 */
	private final int numTasksUsed;
	/**
	 * The number of total tasks held. -1 if about a work period.
	 */
	private final int numTasksTotal;
	/**
	 * Number of spans
	 */
	private final int numSpans;
	/**
	 * Average number of spans per period. -1 if about a work period.
	 */
	private final double numSpansPerPeriodAverage;
	/**
	 * If all tasks held are complete
	 */
	private final boolean allComplete;
	/**
	 * The average length of time for spans.
	 */
	private final Duration averageSpanLength;
	/**
	 * The average length of time fot work periods. Null if about a work period
	 */
	private final Duration averageWorkPeriodLength;
	
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
