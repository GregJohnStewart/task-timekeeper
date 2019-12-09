package com.gjs.taskTimekeeper.baseCode.stats.processor;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;
import com.gjs.taskTimekeeper.baseCode.stats.results.OverallResults;
import com.gjs.taskTimekeeper.baseCode.stats.results.OverallResults.OverallResultsBuilder;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.TreeSet;

/** Stat processor for overall stats on a time manager. TODO:: test */
public class OverallStatProcessor extends StatProcessor<OverallResults> {

    @Override
    public OverallResults process(TimeManager manager) throws StatProcessingException {
        OverallResultsBuilder builder = OverallResults.builder();

        builder.numTasksTotal(manager.getTasks().size());
        builder.numPeriods(manager.getWorkPeriods().size());

        boolean complete = true;
        Duration total = Duration.ZERO;
        Set<Name> tasksUsed = new TreeSet<>();
        int spanCount = 0;
        long numSecsInAllTasks = 0;
        for (WorkPeriod period : manager.getWorkPeriods()) {
            if (complete && period.isUnCompleted()) {
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
        // TODO:: what when no work periods?
        builder.numSpansPerPeriodAverage(
                (double) spanCount / (double) manager.getWorkPeriods().size());

        builder.averageSpanLength(Duration.of(numSecsInAllTasks / spanCount, ChronoUnit.SECONDS));
        builder.averageWorkPeriodLength(
                Duration.of(
                        numSecsInAllTasks / manager.getWorkPeriods().size(), ChronoUnit.SECONDS));

        return builder.build();
    }
}
