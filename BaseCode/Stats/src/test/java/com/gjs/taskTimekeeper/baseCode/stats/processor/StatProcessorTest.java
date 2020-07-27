package com.gjs.taskTimekeeper.baseCode.stats.processor;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.Timespan;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class StatProcessorTest <T extends StatProcessor> {
	private final static TimeManager TEST_MANAGER = new TimeManager();
	
	static {
		LocalDateTime now = LocalDateTime.now();
		Task taskOne = new Task("Task One");
		Task taskTwo = new Task("Task Two");
		Task taskThree = new Task("Task Three");
		
		TEST_MANAGER.addWorkPeriod(
			new WorkPeriod(
				Stream.of(
					new Timespan(taskOne, now, now.plusSeconds(10)),
					new Timespan(taskTwo, now, now.plusSeconds(90))
				)
					  .collect(Collectors.toList())));
		TEST_MANAGER.addWorkPeriod(
			new WorkPeriod(
				Stream.of(
					new Timespan(
						taskThree,
						now.plusSeconds(100),
						now.plusSeconds(200)
					))
					  .collect(Collectors.toList())));
	}
	
	public TimeManager getTestManager() {
		return TEST_MANAGER.clone();
	}
	
	protected TimeManager manager = getTestManager();
	protected T processor;
	
	@BeforeEach
	public abstract void setupProcessor();
	
	@Test
	public void getResultsTest() {
		assertFalse(this.processor.getResults().isPresent());
		
		this.processor.process(this.manager);
		
		assertTrue(this.processor.getResults().isPresent());
	}
	
	@Test
	public void resetResultsTest() {
		this.processor.process(this.manager);
		
		this.processor.resetResults();
		
		assertFalse(this.processor.getResults().isPresent());
	}
}
