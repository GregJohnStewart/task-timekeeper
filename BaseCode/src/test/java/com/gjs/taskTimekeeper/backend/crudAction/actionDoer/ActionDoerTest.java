package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.Action;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObject;

public abstract class ActionDoerTest {

	private static final TimeManager testManager;

	static {
		testManager = new TimeManager();

		testManager.addTask(new Task("Test Task"));

		//TODO:: setup manager with data
	}

	protected static TimeManager getTestManager(){
		return testManager.clone();
	}

	protected final KeeperObject keeperObject;

	protected ActionDoerTest(KeeperObject keeperObject) {
		this.keeperObject = keeperObject;
	}

	protected ActionConfig getActionConfig(Action action){
		ActionConfig config = new ActionConfig();

		config.setObjectOperatingOn(this.keeperObject);
		config.setAction(action);

		return config;
	}
}
