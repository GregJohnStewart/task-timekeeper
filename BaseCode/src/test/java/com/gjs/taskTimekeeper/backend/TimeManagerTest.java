package com.gjs.taskTimekeeper.backend;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class TimeManagerTest {
	private static final Task testTask = new Task("Test task");
	private static final Task testTaskTwo = new Task("Test task Two");

	private static final LocalDateTime now = LocalDateTime.now();
	private static final LocalDateTime nowPlusFive = now.plusMinutes(5);
	private static final LocalDateTime nowPlusTen = now.plusMinutes(10);

	@Test
	public void basicsTest(){
		TimeManager manager = new TimeManager();

		TreeSet<Task> tasks = new TreeSet<>();

		manager = new TimeManager(tasks);

		assertEquals(tasks, manager.getTasks());

		TreeSet<WorkPeriod> periods = new TreeSet<>();

		manager = new TimeManager(tasks, periods);

		assertEquals(periods, manager.getWorkPeriods());
	}

	@Test
	public void setWorkPeriods(){
		TimeManager manager = new TimeManager();

		TreeSet<WorkPeriod> periods = new TreeSet<>();

		manager.setWorkPeriods(periods);

		assertTrue(manager.getWorkPeriods().isEmpty());
		assertEquals(periods, manager.getWorkPeriods());
	}

	@Test
	public void setWorkPeriodsWithTrue(){
		TimeManager manager = new TimeManager();
		manager.addTask(testTask);

		TreeSet<WorkPeriod> periods = new TreeSet<>();

		manager.setWorkPeriods(periods, true);
		assertTrue(manager.getTasks().isEmpty());

		WorkPeriod periodOne = new WorkPeriod();
		periodOne.addTimespan(new Timespan(testTask));
		periods.add(periodOne);

		manager.setWorkPeriods(periods, true);
		assertFalse(manager.getTasks().isEmpty());
		assertTrue(manager.getTasks().contains(testTask));

		periods.clear();

		manager.setWorkPeriods(periods, true);
		assertTrue(manager.getTasks().isEmpty());
	}

	@Test(expected = NullPointerException.class)
	public void setWorkPeriodsWithNull(){
		new TimeManager().setWorkPeriods(null);
	}



}