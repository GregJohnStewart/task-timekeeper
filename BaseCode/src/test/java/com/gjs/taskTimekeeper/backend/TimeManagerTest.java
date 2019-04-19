package com.gjs.taskTimekeeper.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class TimeManagerTest {
	private static final Task testTask = new Task("Test task");
	private static final Task testTaskTwo = new Task("Test task Two");

	private static final LocalDateTime now = LocalDateTime.now();
	private static final LocalDateTime nowPlusFive = now.plusMinutes(5);
	private static final LocalDateTime nowPlusTen = now.plusMinutes(10);

	@Test
	public void basicsTest() {
		TimeManager manager = new TimeManager();

		TreeSet<Task> tasks = new TreeSet<>();

		manager = new TimeManager(tasks);

		assertEquals(tasks, manager.getTasks());

		TreeSet<WorkPeriod> periods = new TreeSet<>();

		manager = new TimeManager(tasks, periods);

		assertEquals(periods, manager.getWorkPeriods());
		manager.hashCode();
	}

	@Test
	public void setWorkPeriods() {
		TimeManager manager = new TimeManager();

		TreeSet<WorkPeriod> periods = new TreeSet<>();

		manager.setWorkPeriods(periods);

		assertTrue(manager.getWorkPeriods().isEmpty());
		assertEquals(periods, manager.getWorkPeriods());

		periods.add(new WorkPeriod().addTimespans(new Timespan(testTask)));

		manager.setWorkPeriods(periods);

		assertFalse(manager.getWorkPeriods().isEmpty());
		assertTrue(manager.getTasks().contains(testTask));
	}

	@Test
	public void setWorkPeriodsWithTrue() {
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
	public void setWorkPeriodsWithNull() {
		new TimeManager().setWorkPeriods(null);
	}

	@Test
	public void addWorkPeriod() {
		TimeManager manager = new TimeManager();

		WorkPeriod period = new WorkPeriod().addTimespans(new Timespan(testTask));

		manager.addWorkPeriod(period);

		assertFalse(manager.getWorkPeriods().isEmpty());
		assertTrue(manager.getWorkPeriods().contains(period));
		assertTrue(manager.getTasks().contains(testTask));
	}

	@Test(expected = NullPointerException.class)
	public void addWorkPeriodWithNull() {
		new TimeManager().addWorkPeriod(null);
	}

	@Test
	public void setTasks() {
		TimeManager manager = new TimeManager();

		Set<Task> tasks = new TreeSet<>();

		manager.setTasks(tasks);

		assertEquals(tasks, manager.getTasks());

		tasks.add(testTask);

		manager.setTasks(tasks);

		assertFalse(manager.getTasks().isEmpty());
		assertTrue(manager.getTasks().contains(testTask));
	}

	@Test(expected = NullPointerException.class)
	public void setTasksWithNull() {
		new TimeManager().setTasks(null);
	}

	@Test
	public void addTasks() {
		TimeManager manager = new TimeManager();

		manager.addTask(testTask);

		assertFalse(manager.getTasks().isEmpty());
		assertTrue(manager.getTasks().contains(testTask));
	}

	@Test
	public void cleanupTasks() {
		TimeManager manager = new TimeManager();

		manager.addTask(testTask);
		manager.cleanupTasks();

		assertTrue(manager.getTasks().isEmpty());

		manager.addWorkPeriod(new WorkPeriod().addTimespans(new Timespan(testTask)));
		manager.cleanupTasks();

		assertFalse(manager.getTasks().isEmpty());
		assertTrue(manager.getTasks().contains(testTask));
	}

	@Test
	public void addTask() {
		TimeManager manager = new TimeManager();

		manager.addTask(testTask);

		assertFalse(manager.getTasks().isEmpty());
		assertTrue(manager.getTasks().contains(testTask));
	}

	@Test(expected = NullPointerException.class)
	public void addTaskWithNull() {
		new TimeManager().addTask(null);
	}

	@Test
	public void addTimespan() {
		TimeManager manager = new TimeManager();

		WorkPeriod finishedPeriod = new WorkPeriod();
		Timespan finishedTimespan = new Timespan(testTask, now, nowPlusFive);

		manager.addWorkPeriod(finishedPeriod);

		manager.addTimespan(finishedTimespan);

		assertTrue(finishedPeriod.getTimespans().contains(finishedTimespan));
		assertTrue(manager.getTasks().contains(testTask));

		WorkPeriod unfinishedPeriod = new WorkPeriod();//.addTimespans(new Timespan(testTask, nowPlusFive, nowPlusTen));
		Timespan unfinishedTimespan = new Timespan(testTaskTwo, nowPlusTen);

		manager.addWorkPeriod(unfinishedPeriod);
		manager.addTimespan(unfinishedTimespan);

		assertTrue(unfinishedPeriod.getTimespans().contains(unfinishedTimespan));
		assertTrue(manager.getTasks().contains(testTaskTwo));
	}

	@Test(expected = NullPointerException.class)
	public void addTimespanWithNull() {
		new TimeManager().addTimespan(null);
	}

	@Test(expected = IllegalStateException.class)
	public void addTimespanWithNoPeriods() {
		new TimeManager().addTimespan(new Timespan(testTask));
	}

	@Test
	public void getHasUnfinishedPeriods() {
		TimeManager manager = new TimeManager();

		assertTrue(manager.getUnfinishedPeriods().isEmpty());
		assertFalse(manager.hasUnfinishedPeriods());

		WorkPeriod finishedPeriod = new WorkPeriod().addTimespans(new Timespan(testTask, now, nowPlusFive));
		manager.addWorkPeriod(finishedPeriod);

		assertTrue(manager.getUnfinishedPeriods().isEmpty());
		assertFalse(manager.hasUnfinishedPeriods());

		WorkPeriod unfinishedPeriod = new WorkPeriod();

		manager.addWorkPeriod(unfinishedPeriod);

		assertFalse(manager.getUnfinishedPeriods().isEmpty());
		assertTrue(manager.getUnfinishedPeriods().contains(unfinishedPeriod));
		assertTrue(manager.hasUnfinishedPeriods());


		unfinishedPeriod.addTimespan(new Timespan(testTask, nowPlusFive, nowPlusTen));

		assertTrue(manager.getUnfinishedPeriods().isEmpty());
		assertFalse(manager.hasUnfinishedPeriods());
	}

	@Test
	public void getWorkPeriodsWith() {
		TimeManager manager = new TimeManager();

		assertTrue(manager.getWorkPeriodsWith(testTask).isEmpty());

		WorkPeriod period = new WorkPeriod();
		Timespan spanOne = new Timespan(testTask);
		Timespan spanTwo = new Timespan(testTaskTwo);

		manager.addWorkPeriod(period).addTimespan(spanOne);

		assertFalse(manager.getWorkPeriodsWith(testTask).isEmpty());
		assertTrue(manager.getWorkPeriodsWith(testTask).contains(period));

		manager.addTimespan(spanTwo);

		assertFalse(manager.getWorkPeriodsWith(testTask).isEmpty());
		assertTrue(manager.getWorkPeriodsWith(testTask).contains(period));
		assertEquals(1, manager.getWorkPeriodsWith(testTask).size());
	}

	@Test
	public void getTimespansWith() {
		TimeManager manager = new TimeManager();

		assertTrue(manager.getTimespansWith(testTask).isEmpty());

		WorkPeriod period = new WorkPeriod();
		Timespan spanOne = new Timespan(testTask);
		Timespan spanTwo = new Timespan(testTaskTwo);

		manager.addWorkPeriod(period).addTimespan(spanOne);

		assertFalse(manager.getTimespansWith(testTask).isEmpty());
		assertTrue(manager.getTimespansWith(testTask).contains(spanOne));

		manager.addTimespan(spanTwo);

		assertFalse(manager.getTimespansWith(testTask).isEmpty());
		assertTrue(manager.getTimespansWith(testTask).contains(spanOne));
		assertEquals(1, manager.getTimespansWith(testTask).size());

	}

	@Test
	public void serialization() throws IOException {
		ObjectMapper mapper = TimeManager.MAPPER;

		TimeManager manager = new TimeManager();

		manager.addWorkPeriod(new WorkPeriod());
		manager.addTimespan(new Timespan(testTask));
		manager.addTask(testTaskTwo);

		String serialized = mapper.writeValueAsString(manager);

		TimeManager deserialized = mapper.readValue(serialized, TimeManager.class);

		assertTrue(manager.equals(deserialized));
	}

}