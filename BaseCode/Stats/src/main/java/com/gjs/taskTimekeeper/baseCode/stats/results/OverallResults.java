package com.gjs.taskTimekeeper.baseCode.stats.results;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class OverallResults extends Results {
    /** Total time recorded in the time manager or period. */
    private final Duration totalTime;
    /** The number of periods held. -1 if results about a work period */
    private final int numPeriods;
    /** The number of tasks used in the manager or period */
    private final int numTasksUsed;
    /** The number of total tasks held. -1 if about a work period. */
    private final int numTasksTotal;
    /** Number of spans */
    private final int numSpans;
    /** Average number of spans per period. -1 if about a work period. */
    private final double numSpansPerPeriodAverage;
    /** If all tasks held are complete */
    private final boolean allComplete;
    /** The average length of time for spans. */
    private final Duration averageSpanLength;
    /** The average length of time fot work periods. Null if about a work period */
    private final Duration averageWorkPeriodLength;

    // TODO:: test
    public OverallResults(
            Duration totalTime,
            int numTasksUsed,
            int numSpans,
            boolean allComplete,
            Duration averageSpanLength) {
        this(totalTime, -1, numTasksUsed, -1, numSpans, -1, allComplete, averageSpanLength, null);
    }

    public OverallResultsBuilder builderForWorkPeriod() {
        OverallResultsBuilder builder = builder();

        builder.numPeriods(-1);
        builder.numTasksTotal(-1);
        builder.averageWorkPeriodLength(null);

        return builder;
    }
}
