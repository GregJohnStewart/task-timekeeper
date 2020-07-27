package com.gjs.taskTimekeeper.baseCode.stats.processor;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.stats.stats.PercentStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeSpentOnTaskProcessorTest extends StatProcessorTest<TimeSpentOnTaskProcessor> {
	
	@Override
	@BeforeEach
	public void setupProcessor() {
		this.processor = new TimeSpentOnTaskProcessor();
	}
	
	@Test
	public void process() {
		this.processor.process(this.manager);
		
		PercentStats<Task> results = this.processor.getResults().get();
		
		assertEquals(3, results.getObjects().size());
		assertEquals(
			(Double)5.0, results.getPercentages().get(this.manager.getTaskByName("Task One")));
		assertEquals(
			(Double)45.0,
			results.getPercentages().get(this.manager.getTaskByName("Task Two"))
		);
		assertEquals(
			(Double)50.0,
			results.getPercentages().get(this.manager.getTaskByName("Task Three"))
		);
	}
	
	@Test
	public void processWithWorkPeriod() {
		this.processor.process(this.manager, this.manager.getWorkPeriods().first());
		
		PercentStats<Task> results = this.processor.getResults().get();
		
		assertEquals(2, results.getObjects().size());
		assertEquals(
			(Double)10.0,
			results.getPercentages().get(this.manager.getTaskByName("Task One"))
		);
		assertEquals(
			(Double)90.0,
			results.getPercentages().get(this.manager.getTaskByName("Task Two"))
		);
	}
}
