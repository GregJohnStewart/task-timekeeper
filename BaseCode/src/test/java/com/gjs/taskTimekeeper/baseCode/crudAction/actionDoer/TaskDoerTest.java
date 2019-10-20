package com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.Task;
import com.gjs.taskTimekeeper.baseCode.TimeManager;
import com.gjs.taskTimekeeper.baseCode.Timespan;
import com.gjs.taskTimekeeper.baseCode.crudAction.Action;
import com.gjs.taskTimekeeper.baseCode.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.utils.Name;
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

		config.setName(newTaskName);

		assertTrue(manager.doCrudAction(config));
		assertNotEquals(orig, manager);

		Task newTask = manager.getTaskByName(newTaskName);

		assertNotNull(newTask);
	}

	@Test
	public void addWithNameAndAtt() {
		ActionConfig config = this.getActionConfig(Action.ADD);
		String newTaskName = "New Test Task";
		String newTaskAttKey = "keyyy";
		String newTaskAttVal = "valueable info";

		config.setName(newTaskName);
		config.setAttributeName(newTaskAttKey);
		config.setAttributeVal(newTaskAttVal);

		assertTrue(manager.doCrudAction(config));
		assertNotEquals(orig, manager);

		Task newTask = manager.getTaskByName(newTaskName);

		assertNotNull(newTask);
		assertTrue(newTask.getAttributes().containsKey(newTaskAttKey));
		assertEquals(newTaskAttVal, newTask.getAttributes().get(newTaskAttKey));

	}
	@Test
	public void addWithNameAndAtts() {
		ActionConfig config = this.getActionConfig(Action.ADD);
		String newTaskName = "New Test Task";

		config.setName(newTaskName);
		config.setAttributes("attOne,valOne;attTwo,valTwo;");

		assertTrue(manager.doCrudAction(config));
		assertNotEquals(orig, manager);

		Task newTask = manager.getTaskByName(newTaskName);

		assertNotNull(newTask);
		assertEquals("valOne", newTask.getAttributes().get("attOne"));
		assertEquals("valTwo", newTask.getAttributes().get("attTwo"));
	}

	@Test
	public void cantAddWithoutName(){
		ActionConfig config = this.getActionConfig(Action.ADD);

		assertFalse(manager.doCrudAction(config));
		assertEquals(orig, manager);
	}

	@Test
	public void cantAddDuplicateName(){
		ActionConfig config = this.getActionConfig(Action.ADD);

		config.setName(
			manager.getTasks().iterator().next().getName().toString()
		);

		assertFalse(manager.doCrudAction(config));
		assertEquals(orig, manager);
	}
	//</editor-fold>

	//<editor-fold desc="Editing Tests">

	private static void setupEditRequest(ActionConfig config){
		config.setAttributeName("attOne");
		config.setAttributeVal("valTwo");
	}

	@Test
	public void failsEditWithNoNameIndex(){
		ActionConfig config = getActionConfig(Action.EDIT);
		setupEditRequest(config);

		assertFalse(manager.doCrudAction(config));
		assertEquals(orig, manager);
	}

	@Test
	public void failsEditWithBothNameIndex(){
		ActionConfig config = getActionConfig(Action.EDIT);
		setupEditRequest(config);

		config.setName(TASK_ONE_NAME);
		config.setIndex(1);

		assertFalse(manager.doCrudAction(config));
		assertEquals(orig, manager);
	}

	@Test
	public void failsEditWithBadName(){
		ActionConfig config = getActionConfig(Action.EDIT);
		setupEditRequest(config);

		config.setName("some bad name");

		assertFalse(manager.doCrudAction(config));
		assertEquals(orig, manager);
	}

	@Test
	public void failsEditWithBadIndex(){
		ActionConfig config = getActionConfig(Action.EDIT);
		setupEditRequest(config);

		config.setIndex(manager.getTasks().size() + 1);

		assertFalse(manager.doCrudAction(config));
		assertEquals(orig, manager);

		config.setIndex(0);

		assertFalse(manager.doCrudAction(config));
		assertEquals(orig, manager);
	}

	@Test
	public void editWithIndex(){
		//TODO
	}

	@Test
	public void editName(){
		ActionConfig config = getActionConfig(Action.EDIT);

		Task task = manager.getTaskByName(TASK_ONE_NAME);

		config.setName(TASK_ONE_NAME);
		config.setNewName("New task one name");

		assertTrue(manager.doCrudAction(config));
		assertNotEquals(orig, manager);

		assertEquals("New task one name", task.getName().getName());
	}

	@Test
	public void editAttributes(){
		ActionConfig config = getActionConfig(Action.EDIT);

		Task task = manager.getTaskByName(TASK_ONE_NAME);

		config.setName(TASK_ONE_NAME);
		config.setAttributes("attOne,valOne;attTwo,valTwo");

		assertTrue(manager.doCrudAction(config));
		assertNotEquals(orig, manager);

		assertEquals("valOne", task.getAttributes().get("attOne"));
		assertEquals("valTwo", task.getAttributes().get("attTwo"));
	}

	@Test
	public void editAttribute(){
		ActionConfig config = getActionConfig(Action.EDIT);

		Task task = manager.getTaskByName(TASK_ONE_NAME);

		config.setName(TASK_ONE_NAME);
		config.setAttributeName("attOne");
		config.setAttributeVal("new attribute value");

		assertTrue(manager.doCrudAction(config));
		assertNotEquals(orig, manager);

		assertEquals("new attribute value", task.getAttributes().get("attOne"));
	}

	@Test
	public void editAddAttribute(){
		ActionConfig config = getActionConfig(Action.EDIT);

		Task task = manager.getTaskByName(TASK_ONE_NAME);

		config.setName(TASK_ONE_NAME);
		config.setAttributeName("new attribute");
		config.setAttributeVal("new attribute value");

		assertTrue(manager.doCrudAction(config));
		assertNotEquals(orig, manager);

		assertEquals("new attribute value", task.getAttributes().get("new attribute"));
	}

	@Test
	public void editRemoveAttribute(){
		ActionConfig config = getActionConfig(Action.EDIT);

		Task task = manager.getTaskByName(TASK_ONE_NAME);

		config.setName(TASK_ONE_NAME);
		config.setAttributeName("attOne");

		assertTrue(manager.doCrudAction(config));
		assertNotEquals(orig, manager);

		assertFalse(task.getAttributes().containsKey("attOne"));

		//don't die if try to remove attribute that doesn't exist
		this.updateOrig();

		assertFalse(manager.doCrudAction(config));
		assertEquals(orig, manager);

		assertFalse(task.getAttributes().containsKey("attOne"));
	}
	//</editor-fold>

	//<editor-fold desc="Removing Tests">
	@Test
	public void dontRemoveWithoutSpecifyingTask() {
		ActionConfig config = this.getActionConfig(Action.REMOVE);

		assertFalse(manager.doCrudAction(config));
		assertEquals(orig, manager);
	}

	@Test
	public void dontRemoveWithWrongTaskName() {
		ActionConfig config = this.getActionConfig(Action.REMOVE);

		//don't remove anything when wrong task name
		config.setName("Wrong task name");
		assertFalse(manager.doCrudAction(config));
		assertEquals(orig, manager);
	}

	@Test
	public void dontRemoveWithSpanAssociated() {
		ActionConfig config = this.getActionConfig(Action.REMOVE);
		String newTaskName = "New Test Task";
		Task testTask = new Task(newTaskName);
		manager.addTask(testTask);

		//dont remove task that has a span associated with it
		manager.addTimespan(new Timespan(testTask));
		config.setName(newTaskName);
		this.updateOrig();

		assertTrue(manager.getTasks().contains(testTask));
		assertFalse(manager.doCrudAction(config));
		assertTrue(manager.getTasks().contains(testTask));
		assertEquals(orig, manager);
	}

	@Test
	public void remove() {
		ActionConfig config = this.getActionConfig(Action.REMOVE);
		Name newTaskName = new Name("New Test Task");
		Task testTask = new Task(newTaskName);
		manager.addTask(testTask);
		this.updateOrig();

		//remove
		config.setName(newTaskName.getName());
		assertTrue(manager.doCrudAction(config));
		assertNotEquals(orig, manager);

		//test removal with index
		manager = getTestManager();
		manager.addTask(testTask);
		config = this.getActionConfig(Action.REMOVE);

		List<Task> tasks = orig.getCrudOperator().getTaskDoer().search(this.getActionConfig(Action.VIEW));
		for(Task task : tasks){
			if(task.getName().equals(newTaskName)){
				config.setIndex(tasks.indexOf(task) + 1);
				break;
			}
		}

		assertTrue(manager.doCrudAction(config));
		assertEquals(2, manager.getTasks().size());
	}

	@Test
	public void removeWithIndex() {
		ActionConfig config = this.getActionConfig(Action.REMOVE);
		Name newTaskName = new Name("New Test Task");
		Task testTask = new Task(newTaskName);
		manager.addTask(testTask);
		this.updateOrig();

		//test removal with index
		manager = getTestManager();
		manager.addTask(testTask);
		config = this.getActionConfig(Action.REMOVE);

		List<Task> tasks = orig.getCrudOperator().getTaskDoer().search(this.getActionConfig(Action.VIEW));
		for(Task task : tasks){
			if(task.getName().equals(newTaskName)){
				config.setIndex(tasks.indexOf(task) + 1);
				break;
			}
		}

		assertTrue(manager.doCrudAction(config));
		assertEquals(2, manager.getTasks().size());
	}
	//</editor-fold>

	//<editor-fold desc="View/ Search Tests">
	@Test
	public void view() {
		//This prints out to console; meat of this test in search().
		//TODO:: hijack outputter and see what prints?
		manager.doCrudAction(this.getActionConfig(Action.VIEW));
		manager.doCrudAction(this.getActionConfig(Action.VIEW).setName("Test Task"));
	}

	@Test
	public void search(){
		TimeManager manager = getTestManager();
		ActionConfig config = this.getActionConfig(Action.VIEW);

		Collection<Task> results = manager.getCrudOperator().getTaskDoer().search(config);

		assertEquals(manager.getTasks().size(), results.size());

		//TODO:: this but more
	}
	//</editor-fold>
}