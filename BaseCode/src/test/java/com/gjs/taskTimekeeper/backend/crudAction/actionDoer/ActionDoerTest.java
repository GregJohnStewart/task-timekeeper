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

	private static final LocalDateTime now = LocalDateTime.now();
	private static final LocalDateTime nowPlusFive = now.plusMinutes(5);
	private static final LocalDateTime nowPlusTen = now.plusMinutes(10);
	private static final LocalDateTime nowPlusFifteen = now.plusMinutes(15);

	static {
		testManager = new TimeManager();

		Task newTask = new Task("Test Task");
		Task newTaskTwo = new Task("Test Task Two");
		Map<String, String> map = new HashMap<>();
		map.put("attOne", "valOne");

		testManager.addTask(newTask.setAttributes(map));
		testManager.addTask(newTaskTwo);


		testManager.addWorkPeriod(new WorkPeriod().addTimespans(
			new Timespan(newTask, now, nowPlusFive),
			new Timespan(newTaskTwo, nowPlusTen, nowPlusFifteen)
		));

		//TODO:: setup manager with data
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
