package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;


import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

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
		for(Task task : manager.getTasks()){
			if(task.getName().equals(config.getName())){
				LOGGER.warn("Duplicate task name given. Not adding new task.");
				System.err.println("ERROR:: Duplicate task name given.");
				return false;
			}
		}

		Task newTask = new Task(config.getName());

		if(config.getAttributeName() != null){
			if(config.getAttributeVal() != null){
				newTask.getAttributes().put(config.getAttributeName(), config.getAttributeVal());
			}
			if(config.getNewAttributeVal() != null){
				newTask.getAttributes().put(config.getAttributeName(), config.getNewAttributeVal());
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
			if(!editingTask.getName().equals(config.getName())){
				modified = true;
				editingTask.setName(config.getNewName());
			}
		}

		if(config.getAttributeName() != null){
			if(config.getNewAttributeVal() != null){
				if(!config.getNewAttributeVal().equals(editingTask.getAttributes().get(config.getAttributeName()))){
					modified = true;
					editingTask.getAttributes().put(config.getAttributeName(), config.getNewAttributeVal());
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
		if(config.getName() == null){
			LOGGER.warn("No task name given to remove the task.");
			System.err.println("ERROR:: No task name given for the new task.");
			return false;
		}

		Task taskToRemove = manager.getTaskByName(config.getName());
		if(taskToRemove == null){
			LOGGER.info("No task with the name given found.");
			System.out.println("No task with the name given found.");
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
	public void view(TimeManager manager, ActionConfig config) {
		LOGGER.info("Viewing one or more tasks.");

		{
			//TODO:: integrate getAtIndex() into this
			Task task = manager.getTaskByName(config.getName());
			if(task != null){
				LOGGER.debug("Found a task that matched the name.");
				System.out.println("Task:");
				System.out.println("\tName: " + task.getName());

				for(Map.Entry<String, String> att : task.getAttributes().entrySet()){
					System.out.println("\t"+ att.getKey() + ": " + att.getValue());
				}

				//TODO:: list other info? (How many periods/ spans have this task)

				return;
			}
		}

		Collection<Task> results = this.search(manager, config);

		this.printView(results);
	}
}
