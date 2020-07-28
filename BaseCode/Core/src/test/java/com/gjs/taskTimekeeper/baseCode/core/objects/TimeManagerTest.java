package com.gjs.taskTimekeeper.baseCode.core.objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeManagerTest {
	private static final Task testTask = new Task("Test task");
	private static final Task testTaskTwo = new Task("Test task Two");
	
	private static final LocalDateTime now = LocalDateTime.now();
	private static final LocalDateTime nowPlusFive = now.plusMinutes(5);
	private static final LocalDateTime nowPlusTen = now.plusMinutes(10);
	
	@Test
	public void basicsTest() {
		assertEquals(new TimeManager(), new TimeManager());
		
		TimeManager manager = new TimeManager();
		
		manager.toString();
		
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
	
	@Test
	public void setWorkPeriodsWithNull() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new TimeManager().setWorkPeriods(null);
		});
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
	
	@Test
	public void addWorkPeriodWithNull() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new TimeManager().addWorkPeriod(null);
		});
	}
	
	@Test
	public void setTasks() {
		TimeManager manager = new TimeManager();
		
		SortedSet<Task> tasks = new TreeSet<>();
		
		manager.setTasks(tasks);
		
		assertEquals(tasks, manager.getTasks());
		
		tasks.add(testTask);
		
		manager.setTasks(tasks);
		
		assertFalse(manager.getTasks().isEmpty());
		assertTrue(manager.getTasks().contains(testTask));
	}
	
	@Test
	public void setTasksWithNull() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new TimeManager().setTasks(null);
		});
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
	
	@Test
	public void addTaskWithNull() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new TimeManager().addTask(null);
		});
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
		
		WorkPeriod unfinishedPeriod =
			new WorkPeriod(); // .addTimespans(new Timespan(testTask, nowPlusFive, nowPlusTen));
		Timespan unfinishedTimespan = new Timespan(testTaskTwo, nowPlusTen);
		
		manager.addWorkPeriod(unfinishedPeriod);
		manager.addTimespan(unfinishedTimespan);
		
		assertTrue(unfinishedPeriod.getTimespans().contains(unfinishedTimespan));
		assertTrue(manager.getTasks().contains(testTaskTwo));
	}
	
	@Test
	public void addTimespanWithNull() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			new TimeManager().addTimespan(null);
		});
	}
	
	@Test()
	public void addTimespanWithNoPeriods() {
		TimeManager manager = new TimeManager().addTimespan(new Timespan(testTask));
		
		assertFalse(manager.getWorkPeriods().isEmpty());
	}
	
	@Test
	public void getHasUnfinishedPeriods() {
		TimeManager manager = new TimeManager();
		
		assertTrue(manager.getUnfinishedPeriods().isEmpty());
		assertFalse(manager.hasUnfinishedPeriods());
		
		WorkPeriod finishedPeriod =
			new WorkPeriod().addTimespans(new Timespan(testTask, now, nowPlusFive));
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
		ObjectMapper mapper = ObjectMapperUtilities.getDefaultMapper();
		
		TimeManager manager = new TimeManager();
		
		manager.addWorkPeriod(new WorkPeriod());
		manager.addTimespan(new Timespan(testTask));
		manager.addTask(testTaskTwo);
		
		String serialized = mapper.writeValueAsString(manager);
		
		TimeManager deserialized = mapper.readValue(serialized, TimeManager.class);
		
		assertTrue(manager.equals(deserialized));
	}
	
	@Test
	public void testClone() {
		TimeManager manager = new TimeManager();
		
		assertTrue(manager.getUnfinishedPeriods().isEmpty());
		assertFalse(manager.hasUnfinishedPeriods());
		
		WorkPeriod finishedPeriod =
			new WorkPeriod().addTimespans(new Timespan(testTask, now, nowPlusFive));
		manager.addWorkPeriod(finishedPeriod);
		
		assertEquals(manager, manager.clone());
	}
	
	@Test
	public void getTasksByName() {
		TimeManager manager = new TimeManager();
		Task testTask = new Task("Test Task Very Important");
		
		manager.addTask(testTask);
		
		assertEquals(testTask, manager.getTaskByName(testTask.getName()));
		
		List<Task> returned = manager.getTasksByNamePattern("Test Task(.*)");
		assertTrue(returned.contains(testTask));
	}
}
