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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TaskDoerTest extends ActionDoerTest {

	public TaskDoerTest() {
		super(KeeperObjectType.TASK);
	}

	//<editor-fold desc="Adding Tests">
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
	public void addWithNameAndAtts() {
		ActionConfig config = this.getActionConfig(Action.ADD);
		String newTaskName = "New Test Task";

		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();

		config.setName(newTaskName);
		config.setAttributes("attOne,valOne;attTwo,valTwo;");

		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		Task newTask = manager.getTaskByName(newTaskName);

		assertNotNull(newTask);
		assertEquals("valOne", newTask.getAttributes().get("attOne"));
		assertEquals("valTwo", newTask.getAttributes().get("attTwo"));
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
	//</editor-fold>

	//<editor-fold desc="Editing Tests">

	private static void setupEditRequest(ActionConfig config){
		config.setAttributeName("attOne");
		config.setAttributeVal("valTwo");
	}

	@Test
	public void failsEditWithNoNameIndex(){
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();
		ActionConfig config = getActionConfig(Action.EDIT);
		setupEditRequest(config);

		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);
	}

	@Test
	public void failsEditWithBothNameIndex(){
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();
		ActionConfig config = getActionConfig(Action.EDIT);
		setupEditRequest(config);

		config.setName(TASK_ONE_NAME);
		config.setIndex(1);

		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);
	}

	@Test
	public void failsEditWithBadName(){
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();
		ActionConfig config = getActionConfig(Action.EDIT);
		setupEditRequest(config);

		config.setName("some bad name");

		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);
	}

	@Test
	public void failsEditWithBadIndex(){
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();
		ActionConfig config = getActionConfig(Action.EDIT);
		setupEditRequest(config);

		config.setIndex(manager.getTasks().size() + 1);

		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);

		config.setIndex(0);

		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);
	}

	@Test
	public void editWithIndex(){
		//TODO
	}

	@Test
	public void editName(){
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();
		ActionConfig config = getActionConfig(Action.EDIT);

		Task task = manager.getTaskByName(TASK_ONE_NAME);

		config.setName(TASK_ONE_NAME);
		config.setNewName("New task one name");

		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		assertEquals("New task one name", task.getName());
	}

	@Test
	public void editAttributes(){
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();
		ActionConfig config = getActionConfig(Action.EDIT);

		Task task = manager.getTaskByName(TASK_ONE_NAME);

		config.setName(TASK_ONE_NAME);
		config.setAttributes("attOne,valOne;attTwo,valTwo");

		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		assertEquals("valOne", task.getAttributes().get("attOne"));
		assertEquals("valTwo", task.getAttributes().get("attTwo"));
	}

	@Test
	public void editAttribute(){
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();
		ActionConfig config = getActionConfig(Action.EDIT);

		Task task = manager.getTaskByName(TASK_ONE_NAME);

		config.setName(TASK_ONE_NAME);
		config.setAttributeName("attOne");
		config.setAttributeVal("new attribute value");

		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		assertEquals("new attribute value", task.getAttributes().get("attOne"));
	}

	@Test
	public void editAddAttribute(){
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();
		ActionConfig config = getActionConfig(Action.EDIT);

		Task task = manager.getTaskByName(TASK_ONE_NAME);

		config.setName(TASK_ONE_NAME);
		config.setAttributeName("new attribute");
		config.setAttributeVal("new attribute value");

		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		assertEquals("new attribute value", task.getAttributes().get("new attribute"));
	}

	@Test
	public void editRemoveAttribute(){
		TimeManager manager = getTestManager();
		TimeManager managerOrig = manager.clone();
		ActionConfig config = getActionConfig(Action.EDIT);

		Task task = manager.getTaskByName(TASK_ONE_NAME);

		config.setName(TASK_ONE_NAME);
		config.setAttributeName("attOne");

		assertTrue(ActionDoer.doObjAction(manager, config));
		assertNotEquals(managerOrig, manager);

		assertFalse(task.getAttributes().containsKey("attOne"));

		//don't die if try to remove attribute that doesn't exist
		managerOrig = manager.clone();

		assertFalse(ActionDoer.doObjAction(manager, config));
		assertEquals(managerOrig, manager);

		assertFalse(task.getAttributes().containsKey("attOne"));
	}
	//</editor-fold>

	//<editor-fold desc="Removing Tests">
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
	public void removeWithIndex() {
		ActionConfig config = this.getActionConfig(Action.REMOVE);
		String newTaskName = "New Test Task";
		TimeManager manager = getTestManager();
		Task testTask = new Task(newTaskName);
		manager.addTask(testTask);
		TimeManager managerOrig = manager.clone();

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
	//</editor-fold>

	//<editor-fold desc="View/ Search Tests">
	@Test
	public void view() {
		//This prints out to console; meat of this test in search().
		//TODO:: hijack Sytem.out and see what prints?
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
	//</editor-fold>
}