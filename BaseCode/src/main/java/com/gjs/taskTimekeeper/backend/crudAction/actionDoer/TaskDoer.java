package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;


import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.utils.Name;
import com.gjs.taskTimekeeper.backend.utils.OutputLevel;
import com.gjs.taskTimekeeper.backend.utils.Outputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The action doer to handle managing Tasks.
 */
public class TaskDoer extends ActionDoer<Task> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskDoer.class);

	@Override
	protected boolean add(TimeManager manager, ActionConfig config) {
		//ensure we have a name for the new task
		if(config.getName() == null){
			LOGGER.warn("No task name given for the new task. Not adding new task.");
			Outputter.consoleErrorPrintln("ERROR:: No task name given for the new task.");
			return false;
		}
		//check we aren't duplicating names
		try {
			if (manager.getTaskByName(config.getName()) != null) {
				LOGGER.warn("Duplicate task name given. Not adding new task.");
				Outputter.consoleErrorPrintln("ERROR:: Duplicate task name given.");
				return false;
			}
		}catch (Exception e){
			LOGGER.warn("Invalid task name given. Not adding new task.");
			Outputter.consoleErrorPrintln("Invalid task name given. Not adding new task.");
			return false;
		}
		if(config.getAttributeName() != null && config.getAttributes() != null){
			LOGGER.warn("Cannot process both single attribute and set of attributes.");
			Outputter.consoleErrorPrintln("Cannot process both single attribute and set of attributes.");
			return false;
		}

		Task newTask = new Task(new Name(config.getName()));

		if(config.getAttributeName() != null){
			if(config.getAttributeVal() != null){
				newTask.getAttributes().put(config.getAttributeName(), config.getAttributeVal());
			}
		}
		if(config.getAttributes() != null){
			Map<String, String> newAtts;
			try{
				newAtts = ActionDoer.parseAttributes(config.getAttributes());
			}catch (IllegalArgumentException e){
				LOGGER.warn("Attribute string given was invalid. Error: ", e);
				Outputter.consoleErrorPrintln("Attribute string given was invalid. Error: "+ e.getMessage());
				return false;
			}
			newTask.setAttributes(newAtts);
		}

		Outputter.consolePrintln(OutputLevel.VERBOSE, "New task details:");
		Outputter.consolePrintln(OutputLevel.VERBOSE, "\tName: " + newTask.getName());
		if(!newTask.getAttributes().isEmpty()){
			Outputter.consolePrintln(OutputLevel.VERBOSE, "\t(custom attribute) " + config.getAttributeName() + ": " + config.getAttributeVal());
		}

		manager.addTask(newTask);
		Outputter.consolePrintln(OutputLevel.DEFAULT, "New task added.");
		return true;
	}

	@Override
	protected boolean edit(TimeManager manager, ActionConfig config) {
		Task editingTask = null;
		if(config.getName() != null && config.getIndex() != null){
			LOGGER.warn("Error: Both name and search index were used to specify which task to edit.");
			Outputter.consoleErrorPrintln("ERROR:: Cannot give both name and index to specify which task to edit.");
			return false;
		}else if(config.getName() != null){
			editingTask = manager.getTaskByName(config.getName());
		}else if(config.getIndex() != null){
			int index = config.getIndex() - 1;
			List<Task> searchResults = this.search(manager, config);
			if(index >= 0 && index < searchResults.size()) {
				editingTask = this.search(manager, config).get(index);
				Outputter.consolePrintln(OutputLevel.DEFAULT, "Editing task: " + editingTask.getName());
			}else{
				LOGGER.warn("Index given was out of bounds for referencing tasks.");
				Outputter.consoleErrorPrintln("ERROR: Index given was out of bounds.");
				return false;
			}
		}else{
			LOGGER.warn("No task name or index entered to look up task to change.");
			Outputter.consoleErrorPrintln("ERROR:: Nothing given to specify which task to edit.");
			return false;
		}
		if(editingTask == null){
			LOGGER.warn("No task found with name or index given.");
			Outputter.consoleErrorPrintln("No task found to edit.");
			return false;
		}
		if(config.getAttributeName() != null && config.getAttributes() != null){
			LOGGER.warn("Cannot process both single attribute and set of attributes.");
			Outputter.consoleErrorPrintln("Cannot process both single attribute and set of attributes.");
			return false;
		}

		boolean modified = false;
		if(config.getNewName() != null){
			try{
				manager.updateTaskName(editingTask, new Name(config.getNewName()));
				modified = true;
				Outputter.consolePrintln(OutputLevel.VERBOSE, "New name set to: " + editingTask.getName());
			}catch (IllegalArgumentException e){
				LOGGER.warn("Invalid new name given. ", e);
				Outputter.consoleErrorPrintln("Invalid new name given. Error: \n"+e.getMessage());
				return false;
			}
		}

		if(config.getAttributeName() != null){
			if(config.getAttributeVal() != null){
				if(!config.getAttributeVal().equals(editingTask.getAttributes().get(config.getAttributeName()))){
					modified = true;
					editingTask.getAttributes().put(config.getAttributeName(), config.getAttributeVal());
					Outputter.consolePrintln(OutputLevel.VERBOSE, "Set attribute " + config.getAttributeName() + " to " + config.getAttributeVal());
				}
			}else{
				if(editingTask.getAttributes().containsKey(config.getAttributeName())) {
					modified = true;
					editingTask.getAttributes()
						.remove(config.getAttributeName());
					Outputter.consolePrintln(OutputLevel.VERBOSE, "Removed attribute: " + config.getAttributeName());
				}
			}
		}
		if(config.getAttributes() != null){
			Map<String, String> newAtts;
			try{
				newAtts = ActionDoer.parseAttributes(config.getAttributes());
			}catch (IllegalArgumentException e){
				LOGGER.warn("Attribute string given was invalid. Error: ", e);
				Outputter.consoleErrorPrintln("Attribute string given was invalid. Error: "+ e.getMessage());
				return false;
			}
			if(!editingTask.getAttributes().equals(newAtts)) {
				editingTask.setAttributes(newAtts);
				modified = true;
			}else{
				LOGGER.debug("Attribute map given same as what was already held.");
			}
		}

		if(!modified){
			Outputter.consolePrintln(OutputLevel.DEFAULT, "Task not modified.");
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
			Outputter.consoleErrorPrintln("No task with the name given found or at index given.");
			return false;
		}

		if(!manager.getTimespansWith(taskToRemove).isEmpty()){
			//TODO:: test
			LOGGER.warn("Task part of one or more time spans. Cannot remove task.");
			Outputter.consoleErrorPrintln("Task given part of one or more time spans. Cannot remove.");
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
		output.add(object.getName().toString());

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
		Collections.reverse(output);
		return output;
	}

	@Override
	public void displayOne(TimeManager manager, Task task) {
		Outputter.consolePrintln(OutputLevel.DEFAULT, "Task:");
		Outputter.consolePrintln(OutputLevel.DEFAULT, "\tName: " + task.getName());

		if(task.getAttributes().isEmpty()){
			Outputter.consolePrintln(OutputLevel.DEFAULT, "\tNo Attributes");
		}else {
			Outputter.consolePrintln(OutputLevel.DEFAULT, "\tAttributes:");
			for (Map.Entry<String, String> att : task.getAttributes()
				                                     .entrySet()) {
				Outputter.consolePrintln(OutputLevel.DEFAULT, "\t\t" + att.getKey() + ": " + att.getValue());
			}
		}

		Outputter.consolePrintln(OutputLevel.DEFAULT, "\tPeriods with task: " + manager.getWorkPeriodsWith(task).size());
		Outputter.consolePrintln(OutputLevel.DEFAULT, "\tSpans with task: " + manager.getTimespansWith(task).size());
	}

	@Override
	public void view(TimeManager manager, ActionConfig config) {
		LOGGER.info("Viewing one or more tasks.");

		{
			if(config.getName() != null) {
				Task task = manager.getTaskByName(config.getName());
				if (task != null) {
					LOGGER.debug("Found a task that matched the name.");
					this.displayOne(manager, task);
					return;
				}
			}else {
				Task task = this.getAtIndex(manager, config);

				if (task != null) {
					LOGGER.debug("Found a task at the given index.");
					this.displayOne(manager, task);
					return;
				}
			}
		}

		List<Task> results = this.search(manager, config);

		this.printView("Tasks", results);
	}
}
