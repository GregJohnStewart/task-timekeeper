package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.Timespan;
import com.gjs.taskTimekeeper.backend.crudAction.Action;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TaskDoerTest extends ActionDoerTest {

	public TaskDoerTest() {
		super(KeeperObjectType.TASK);
	}

	@Test
	public void add() {
		ActionConfig config = this.getActionConfig(Action.ADD);
		String newTaskName = "New Test Task";
		String newTaskAttKey = "keyyy";
		String newTaskAttVal = "valueable info";
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();

		//check that we need task name
		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);

		//give task name and verify it was added
		config.setName(newTaskName);
		config.setAttributeName(newTaskAttKey);
		config.setAttributeVal(newTaskAttVal);

		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		boolean found = false;
		for(Task task : manager.getTasks()){
			if(
				newTaskName.equals(task.getName()) &&
				task.getAttributes().containsKey(newTaskAttKey) &&
				newTaskAttVal.equals(task.getAttributes().get(newTaskAttKey))
			){
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
		String taskName = "New Task";
		String newTaskName = "Another Task";
		Task workingTask = new Task(taskName);
		TimeManager manager = new TimeManager();

		manager.addTask(workingTask);

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.EDIT)
					.setName(taskName)
					.setNewName(newTaskName)
					.setAttributeName("key")
					.setAttributeVal("value")
			)
		);

		assertEquals(newTaskName, workingTask.getName());
		assertTrue(workingTask.getAttributes().containsKey("key"));
		assertEquals("value", workingTask.getAttributes().get("key"));

		assertTrue(
			ActionDoer.doObjAction(
				manager,
				this.getActionConfig(Action.EDIT)
					.setName(newTaskName)
					.setAttributeName("key")
			)
		);

		assertFalse(workingTask.getAttributes().containsKey("key"));
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
		config.setName("Wrong task name");
		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);

		//remove
		config.setName(newTaskName);
		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		manager.addTimespan(new Timespan(testTask));

		assertTrue(manager.getTasks().contains(testTask));
		assertFalse(ActionDoer.doObjAction(manager, config));

		//TODO:: test removal with index
	}

	@Test
	public void view() {
		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW));
		ActionDoer.doObjAction(getTestManager(), this.getActionConfig(Action.VIEW).setName("Test Task"));
	}

	@Test
	public void search(){
		TimeManager manager = getTestManager();
		ActionConfig config = this.getActionConfig(Action.VIEW);

		Collection<Task> results = new TaskDoer().search(manager, config);

		assertEquals(manager.getTasks().size(), results.size());

		//TODO:: this but more
	}
}