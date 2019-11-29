package com.gjs.taskTimekeeper.baseCode.stats.processor;

import static org.junit.Assert.assertEquals;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.stats.results.PercentResults;
import org.junit.Test;

public class TimeSpentOnTaskProcessorTest extends StatProcessorTest<TimeSpentOnTaskProcessor> {

    @Override
    public void setupProcessor() {
        this.processor = new TimeSpentOnTaskProcessor();
    }

    @Test
    public void process() {
        this.processor.process(this.manager);

        PercentResults<Task> results = this.processor.getResults().get();

        assertEquals(3, results.getObjects().size());
        assertEquals(
                (Double) 5.0, results.getPercentages().get(this.manager.getTaskByName("Task One")));
        assertEquals(
                (Double) 45.0,
                results.getPercentages().get(this.manager.getTaskByName("Task Two")));
        assertEquals(
                (Double) 50.0,
                results.getPercentages().get(this.manager.getTaskByName("Task Three")));
    }

    @Test
    public void processWithWorkPeriod() {
        this.processor.process(this.manager, this.manager.getWorkPeriods().first());

        PercentResults<Task> results = this.processor.getResults().get();

        assertEquals(2, results.getObjects().size());
        assertEquals(
                (Double) 10.0,
                results.getPercentages().get(this.manager.getTaskByName("Task One")));
        assertEquals(
                (Double) 90.0,
                results.getPercentages().get(this.manager.getTaskByName("Task Two")));
    }
}
