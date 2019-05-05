package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;


import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskDoer extends ActionDoer {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskDoer.class);

	@Override
	protected boolean add(TimeManager manager, ActionConfig config) {
		//ensure we have a name for the new task
		if(config.getTaskname() == null){
			LOGGER.warn("No task name given for the new task. Not adding new task.");
			System.err.println("ERROR:: No task name given for the new task.");
			return false;
		}
		//check we aren't duplicating names
		for(Task task : manager.getTasks()){
			if(task.getName().equals(config.getTaskname())){
				LOGGER.warn("Duplicate task name given. Not adding new task.");
				System.err.println("ERROR:: Duplicate task name given.");
				return false;
			}
		}

		Task newTask = new Task(config.getTaskname());

		//TODO:: add more datapoints to add to the task

		manager.addTask(newTask);
		return true;
	}

	@Override
	protected boolean edit(TimeManager manager, ActionConfig config) {
		//TODO:: this
		return false;
	}

	@Override
	protected boolean remove(TimeManager manager, ActionConfig config) {
		if(config.getTaskname() == null){
			LOGGER.warn("No task name given to remove the task.");
			System.err.println("ERROR:: No task name given for the new task.");
			return false;
		}

		Task taskToRemove = null;
		for(Task task : manager.getTasks()){
			if(config.getTaskname().equals(task.getName())){
				taskToRemove = task;
			}
		}
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
	public void view(TimeManager manager, ActionConfig config) {
		LOGGER.info("Viewing one or more tasks.");
		//TODO:: nicely output task data, based on the config
	}
}
