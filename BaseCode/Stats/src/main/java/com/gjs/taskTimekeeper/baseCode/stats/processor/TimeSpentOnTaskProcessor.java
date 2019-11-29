package com.gjs.taskTimekeeper.baseCode.stats.processor;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.stats.results.PercentResults;
import java.util.HashMap;
import java.util.Map;

public class TimeSpentOnTaskProcessor extends StatProcessor<PercentResults<Task>> {
    @Override
    public PercentResults<Task> process(TimeManager manager) throws StatProcessingException {
        Map<Task, Number> resultMap =
                new HashMap<>(); // more efficient to make map and then create results object

        for (Task task : manager.getTasks()) {
            long duration = 0;
            for (WorkPeriod period : manager.getWorkPeriodsWith(task)) {
                duration += period.getTotalTimeWith(task.getName()).getSeconds();
            }
            resultMap.put(task, duration);
        }

        return this.setResults(new PercentResults<Task>(resultMap));
    }
}
