package com.gjs.taskTimekeeper.baseCode.stats.processor;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;
import com.gjs.taskTimekeeper.baseCode.stats.results.PercentResults;
import java.util.HashMap;
import java.util.Map;

public class TimeSpentOnTaskProcessor extends StatProcessor<PercentResults<Task>> {
    @Override
    public PercentResults<Task> process(TimeManager manager) throws StatProcessingException {
        // more efficient to make map and then create results object
        Map<Task, Number> resultMap = new HashMap<>();

        for (Task task : manager.getTasks()) {
            long duration = 0;
            for (WorkPeriod period : manager.getWorkPeriodsWith(task)) {
                duration += period.getTotalTimeWith(task.getName()).getSeconds();
            }
            resultMap.put(task, duration);
        }

        return this.setResults(new PercentResults<Task>(resultMap));
    }

    public PercentResults<Task> process(TimeManager manager, WorkPeriod period)
            throws StatProcessingException {
        if (!manager.getWorkPeriods().contains(period)) {
            throw new StatProcessingException("Work period not in manager given.");
        }
        // more efficient to make map and then create results object
        Map<Task, Number> resultMap = new HashMap<>();

        for (Name taskName : period.getTaskNames()) {
            Task task = manager.getTaskByName(taskName);
            resultMap.put(task, period.getTotalTimeWith(task.getName()).getSeconds());
        }

        return this.setResults(new PercentResults<Task>(resultMap));
    }
}
