package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;


import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The actiuon doer to handle managing Tasks.
 */
public class TaskDoer extends ActionDoer<Task> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskDoer.class);

	@Override
	protected boolean add(TimeManager manager, ActionConfig config) {
		//ensure we have a name for the new task
		if(config.getName() == null){
			LOGGER.warn("No task name given for the new task. Not adding new task.");
			System.err.println("ERROR:: No task name given for the new task.");
			return false;
		}
		//check we aren't duplicating names
		if(manager.getTaskByName(config.getName()) != null){
			LOGGER.warn("Duplicate task name given. Not adding new task.");
			System.err.println("ERROR:: Duplicate task name given.");
			return false;
		}

		Task newTask = new Task(config.getName());

		if(config.getAttributeName() != null){
			if(config.getAttributeVal() != null){
				newTask.getAttributes().put(config.getAttributeName(), config.getAttributeVal());
			}
		}

		manager.addTask(newTask);
		return true;
	}

	@Override
	protected boolean edit(TimeManager manager, ActionConfig config) {
		//ensure we have old and new name
		if(config.getName() == null){
			LOGGER.warn("No task name given for the task to change. Not editing task.");
			System.err.println("ERROR:: No task name given for the changing task. Not editing task.");
			return false;
		}

		Task editingTask = manager.getTaskByName(config.getName());
		if(editingTask == null){
			LOGGER.info("No task with the name given found.");
			System.out.println("No task with the name given found.");
			return false;
		}

		boolean modified = false;
		if(config.getNewName() != null){
			if(!editingTask.getName().equals(config.getNewName())){
				modified = true;
				editingTask.setName(config.getNewName());
			}
		}

		if(config.getNewAttributeName() != null){
			if(config.getNewAttributeVal() != null){
				if(!config.getNewAttributeVal().equals(editingTask.getAttributes().get(config.getNewAttributeName()))){
					modified = true;
					editingTask.getAttributes().put(config.getNewAttributeName(), config.getNewAttributeVal());
				}
			}else{
				modified = true;
				editingTask.getAttributes().remove(config.getNewAttributeName());
			}
		}

		if(config.getAttributeName() != null){
			if(config.getAttributeVal() != null){
				if(!config.getAttributeVal().equals(editingTask.getAttributes().get(config.getAttributeName()))){
					modified = true;
					editingTask.getAttributes().put(config.getAttributeName(), config.getAttributeVal());
				}
			}else{
				modified = true;
				editingTask.getAttributes().remove(config.getAttributeName());
			}
		}

		return modified;
	}

	@Override
	protected boolean remove(TimeManager manager, ActionConfig config) {
		Task taskToRemove = null;

		if(config.getName() != null){
			taskToRemove = manager.getTaskByName(config.getName());
		}

		if(config.getIndex() != null) {
			taskToRemove = this.getAtIndex(manager, config);
		}

		if(taskToRemove == null){
			LOGGER.info("No task with the name given found or at index given.");
			System.out.println("No task with the name given found or at index given.");
			return false;
		}

		if(!manager.getTimespansWith(taskToRemove).isEmpty()){
			LOGGER.warn("Task part of one or more time spans. Cannot remove task.");
			System.err.println("Task given part of one or more time spans. Cannot remove.");
			return false;
		}

		manager.getTasks().remove(taskToRemove);
		return true;
	}

	@Override
	public List<String> getViewHeaders() {
		List<String> output = new ArrayList<>();

		output.add("#");
		output.add("Name");

		return output;
	}

	@Override
	public List<String> getViewRowEntries(int rowNum, Task object) {
		List<String> output = new ArrayList<>();

		output.add("" + rowNum);
		output.add(object.getName());

		return output;
	}

	@Override
	public List<Task> search(TimeManager manager, ActionConfig config) {
		List<Task> output = null;

		if(config.getName() != null){
			output = manager.getTasksByNamePattern(config.getName());
		}else{
			output = new LinkedList<>(manager.getTasks());
		}

		if(config.getAttributeName() != null && config.getAttributeVal() != null){
			output = output.stream().filter(
				(Task task) -> {
					if(task.getAttributes().containsKey(config.getAttributeName())){
						return config.getAttributeVal().equals(task.getAttributes().get(config.getAttributeName()));
					}
					return false;
				}
			).collect(Collectors.toList());
		}

		return output;
	}

	@Override
	public void displayOne(TimeManager manager, Task task) {
		System.out.println("Task:");
		System.out.println("\tName: " + task.getName());

		for(Map.Entry<String, String> att : task.getAttributes().entrySet()){
			System.out.println("\t"+ att.getKey() + ": " + att.getValue());
		}

		System.out.println("\tPeriods with task: " + manager.getWorkPeriodsWith(task).size());
		System.out.println("\tSpans with task: " + manager.getTimespansWith(task).size());

	}

	@Override
	public void view(TimeManager manager, ActionConfig config) {
		LOGGER.info("Viewing one or more tasks.");

		{
			Task task = manager.getTaskByName(config.getName());
			if(task != null){
				LOGGER.debug("Found a task that matched the name.");
				this.displayOne(manager, task);
				return;
			}
			task = this.getAtIndex(manager, config);

			if(task != null){
				LOGGER.debug("Found a task at the given index.");
				this.displayOne(manager, task);
				return;
			}
		}

		List<Task> results = this.search(manager, config);

		this.printView("Tasks", results);
	}
}
