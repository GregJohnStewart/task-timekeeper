package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.Action;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.Timespan;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class ActionDoerExtendingTest {
	
	private static final TimeManager testManager;
	
	protected static final LocalDateTime now = LocalDateTime.now();
	protected static final LocalDateTime nowPlusFive = now.plusMinutes(5);
	protected static final LocalDateTime nowPlusTen = now.plusMinutes(10);
	protected static final LocalDateTime nowPlusFifteen = now.plusMinutes(15);
	protected static final LocalDateTime nowPlusHour = now.plusMinutes(60);
	protected static final LocalDateTime nowPlusHourFive = now.plusMinutes(65);
	protected static final LocalDateTime nowPlusHourTen = now.plusMinutes(70);
	protected static final LocalDateTime nowPlusHourFifteen = now.plusMinutes(75);
	protected static final LocalDateTime nowPlusTwoHour = now.plusMinutes(120);
	protected static final LocalDateTime nowPlusTwoHourFive = now.plusMinutes(125);
	protected static final LocalDateTime nowPlusTwoHourTen = now.plusMinutes(130);
	protected static final LocalDateTime nowPlusTwoHourFifteen = now.plusMinutes(135);
	
	protected static final String TASK_ONE_NAME = "aa Test Task";
	protected static final String TASK_TWO_NAME = "bb Test Task Two";
	
	static {
		testManager = new TimeManager();
		
		Task newTask = new Task(TASK_ONE_NAME);
		Task newTaskTwo = new Task(TASK_TWO_NAME);
		Map<String, String> map = new HashMap<>();
		map.put("attOne", "valOne");
		
		testManager.addTask(newTask.setAttributes(map));
		testManager.addTask(newTaskTwo);
		
		testManager.addWorkPeriod(
			new WorkPeriod()
				.addTimespans(
					new Timespan(newTask, now, nowPlusFive),
					new Timespan(newTaskTwo, nowPlusTen, nowPlusFifteen)
				)
				.setAttributes(map));
		
		testManager.addWorkPeriod(
			new WorkPeriod()
				.addTimespans(
					new Timespan(newTaskTwo, nowPlusHour, nowPlusHourFive),
					new Timespan(newTask, nowPlusHourTen, nowPlusHourFifteen)
				));
		
		testManager.addWorkPeriod(
			new WorkPeriod()
				.addTimespans(
					new Timespan(newTaskTwo, nowPlusTwoHour, nowPlusTwoHourFive),
					new Timespan(newTask, nowPlusTwoHourTen)
				));
	}
	
	protected static TimeManager getTestManager() {
		return testManager.clone();
	}
	
	protected final KeeperObjectType keeperObject;
	
	protected ActionDoerExtendingTest(KeeperObjectType keeperObject) {
		this.keeperObject = keeperObject;
	}
	
	protected ActionConfig getActionConfig(Action action) {
		ActionConfig config = new ActionConfig();
		
		config.setObjectOperatingOn(this.keeperObject);
		config.setAction(action);
		
		return config;
	}
	
	protected ByteArrayOutputStream printStream = new ByteArrayOutputStream();
	protected ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
	protected Outputter outputter = new Outputter(printStream, errorStream);
	protected TimeManager manager = getTestManager();
	protected TimeManager orig = manager.clone();
	
	{
		this.manager.getCrudOperator().setOutputter(outputter);
	}
	
	protected void updateOrig(TimeManager manager) {
		this.orig = manager.clone();
	}
	
	protected void updateOrig() {
		this.updateOrig(this.manager);
	}
	
	protected void createNewPeriodAndSelect() {
		manager.doCrudAction(
			this.getActionConfig(Action.ADD).setObjectOperatingOn(KeeperObjectType.PERIOD));
		this.selectPeriodAt(1);
		this.updateOrig();
	}
	
	protected void selectPeriodAt(int index) {
		manager.doCrudAction(
			new ActionConfig(KeeperObjectType.PERIOD, Action.VIEW)
				.setSelect(true)
				.setIndex(index));
	}
	
	private static void assertOutputContains(ByteArrayOutputStream os, String... parts) {
		if(parts.length == 0) {
			assertEquals(0, os.size());
		}
		String output = os.toString();
		
		for(String part : parts) {
			assertTrue(output.contains(part));
		}
	}
	
	protected void assertOutputContains(String... parts) {
		assertOutputContains(this.printStream, parts);
	}
	
	protected void assertErrOutputContains(String... parts) {
		assertOutputContains(this.errorStream, parts);
	}
	
	protected void resetPrintStreams() {
		this.printStream.reset();
		this.errorStream.reset();
	}
	
	@BeforeEach
	public void setOutputterVerbosity() {
		manager.getCrudOperator().setOutputLevelThreshold(OutputLevel.VERBOSE);
	}
}
