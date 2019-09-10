package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.Timespan;
import com.gjs.taskTimekeeper.backend.WorkPeriod;
import com.gjs.taskTimekeeper.backend.crudAction.Action;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class ActionDoerTest {

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

	protected static final String TASK_ONE_NAME = "Test Task";
	protected static final String TASK_TWO_NAME = "Test Task Two";

	static {
		testManager = new TimeManager();

		Task newTask = new Task(TASK_ONE_NAME);
		Task newTaskTwo = new Task(TASK_TWO_NAME);
		Map<String, String> map = new HashMap<>();
		map.put("attOne", "valOne");

		testManager.addTask(newTask.setAttributes(map));
		testManager.addTask(newTaskTwo);


		testManager.addWorkPeriod(new WorkPeriod().addTimespans(
			new Timespan(newTask, now, nowPlusFive),
			new Timespan(newTaskTwo, nowPlusTen, nowPlusFifteen)
		).setAttributes(map));

		testManager.addWorkPeriod(new WorkPeriod().addTimespans(
			new Timespan(newTaskTwo, nowPlusHour, nowPlusHourFive),
			new Timespan(newTask, nowPlusHourTen, nowPlusHourFifteen)
		));

		testManager.addWorkPeriod(new WorkPeriod().addTimespans(
			new Timespan(newTaskTwo, nowPlusTwoHour, nowPlusTwoHourFive),
			new Timespan(newTask, nowPlusTwoHourTen)
		));
	}

	protected static TimeManager getTestManager(){
		return testManager.clone();
	}

	protected final KeeperObjectType keeperObject;

	protected ActionDoerTest(KeeperObjectType keeperObject) {
		this.keeperObject = keeperObject;
	}

	protected ActionConfig getActionConfig(Action action){
		ActionConfig config = new ActionConfig();

		config.setObjectOperatingOn(this.keeperObject);
		config.setAction(action);

		return config;
	}
}
