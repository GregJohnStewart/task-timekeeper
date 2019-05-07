package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.Timespan;
import com.gjs.taskTimekeeper.backend.crudAction.Action;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaskDoerTest extends ActionDoerTest {

	public TaskDoerTest() {
		super(KeeperObjectType.TASK);
	}

	@Test
	public void add() {
		ActionConfig config = this.getActionConfig(Action.ADD);
		String newTaskName = "New Test Task";
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();

		//check that we need task name
		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);

		//give task name and verify it was added
		config.setTaskName(newTaskName);

		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		boolean found = false;
		for(Task task : manager.getTasks()){
			if(newTaskName.equals(task.getName())){
				found = true;
				break;
			}
		}
		assertTrue(found);

		//test that we can't add a duplicate name
		assertFalse(ActionDoer.doObjAction(manager, config));
	}

	@Test
	public void edit() {
		//TODO:: this
	}

	@Test
	public void remove() {
		ActionConfig config = this.getActionConfig(Action.REMOVE);
		String newTaskName = "New Test Task";
		TimeManager manager = getTestManager();
		Task testTask = new Task(newTaskName);
		manager.addTask(testTask);
		TimeManager managerOrig = manager.clone();

		//don't remove w/o specifying task
		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);

		//don't remove anything when wrong task name
		config.setTaskName("Wrong task name");
		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);

		//remove
		config.setTaskName(newTaskName);
		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		manager.addTimespan(new Timespan(testTask));

		assertTrue(manager.getTasks().contains(testTask));
		assertFalse(ActionDoer.doObjAction(manager, config));
	}

	@Test
	public void view() {
		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW));
	}
}