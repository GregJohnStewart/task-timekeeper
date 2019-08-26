package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.Timespan;
import com.gjs.taskTimekeeper.backend.crudAction.Action;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class TaskDoerTest extends ActionDoerTest {

	public TaskDoerTest() {
		super(KeeperObjectType.TASK);
	}

	@Test
	public void addWithJustName(){
		ActionConfig config = this.getActionConfig(Action.ADD);
		String newTaskName = "New Test Task";
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();

		config.setName(newTaskName);

		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		Task newTask = manager.getTaskByName(newTaskName);

		assertNotNull(newTask);
	}

	@Test
	public void addWithNameAndAtt() {
		ActionConfig config = this.getActionConfig(Action.ADD);
		String newTaskName = "New Test Task";
		String newTaskAttKey = "keyyy";
		String newTaskAttVal = "valueable info";
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();

		config.setName(newTaskName);
		config.setAttributeName(newTaskAttKey);
		config.setAttributeVal(newTaskAttVal);

		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		Task newTask = manager.getTaskByName(newTaskName);

		assertNotNull(newTask);
		assertTrue(newTask.getAttributes().containsKey(newTaskAttKey));
		assertEquals(newTaskAttVal, newTask.getAttributes().get(newTaskAttKey));

	}


	@Test
	public void cantAddWithoutName(){
		ActionConfig config = this.getActionConfig(Action.ADD);
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();

		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);
	}

	@Test
	public void cantAddDuplicateName(){
		ActionConfig config = this.getActionConfig(Action.ADD);
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();

		config.setName(
			manager.getTasks().iterator().next().getName()
		);

		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);
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
	public void dontRemoveWithoutSpecifyingTask() {
		ActionConfig config = this.getActionConfig(Action.REMOVE);
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();

		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);
	}

	@Test
	public void dontRemoveWithWrongTaskName() {
		ActionConfig config = this.getActionConfig(Action.REMOVE);
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();

		//don't remove anything when wrong task name
		config.setName("Wrong task name");
		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);
	}

	@Test
	public void dontRemoveWithSpanAssociated() {
		ActionConfig config = this.getActionConfig(Action.REMOVE);
		String newTaskName = "New Test Task";
		TimeManager manager = getTestManager();
		Task testTask = new Task(newTaskName);
		manager.addTask(testTask);
		TimeManager managerOrig = manager.clone();

		//dont remove task that has a span associated with it
		manager.addTimespan(new Timespan(testTask));

		assertTrue(manager.getTasks().contains(testTask));
		assertFalse(ActionDoer.doObjAction(manager, config));
		assertTrue(manager.getTasks().contains(testTask));
	}

	@Test
	public void remove() {
		ActionConfig config = this.getActionConfig(Action.REMOVE);
		String newTaskName = "New Test Task";
		TimeManager manager = getTestManager();
		Task testTask = new Task(newTaskName);
		manager.addTask(testTask);
		TimeManager managerOrig = manager.clone();

		//remove
		config.setName(newTaskName);
		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		//test removal with index
		manager = getTestManager();
		manager.addTask(testTask);
		config = this.getActionConfig(Action.REMOVE);

		List<Task> tasks = new TaskDoer().search(managerOrig, this.getActionConfig(Action.VIEW));
		for(Task task : tasks){
			if(task.getName().equals(newTaskName)){
				config.setIndex(tasks.indexOf(task) + 1);
				break;
			}
		}

		assertTrue(ActionDoer.doObjAction(manager, config));
		assertEquals(2, manager.getTasks().size());
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