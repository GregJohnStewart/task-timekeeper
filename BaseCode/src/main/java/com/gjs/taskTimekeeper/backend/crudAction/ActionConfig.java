package com.gjs.taskTimekeeper.backend.crudAction;

import org.kohsuke.args4j.Option;

public class ActionConfig {
	@Option(name = "-h", aliases = {"--help"}, usage = "Show this help dialogue.")
	private boolean showHelp = false;

	@Option(name = "-q", aliases = {"q", "--quit", "quit"}, usage = "Exits Management mode.")
	private boolean quit = false;

	@Option(name = "-a", aliases = {"a", "--action", "action"}, usage = "The action to take.")
	private Action action;

	@Option(name = "-o", aliases = {"o", "--object", "object"}, usage = "The type of object to operate on.")
	private KeeperObjectType objectOperatingOn;

	//Identifiers/ data inputs to add:
	//  current (for periods/ timespans)
	//  name
	//  key/value pair
	//  time/date spans and instants

	@Option(name="-t", aliases = {"t", "--task", "task"}, usage = "The name of the task to operate on.")
	private String taskName = null;
	@Option(name="-nt", aliases = {"nt", "--newTask", "newTask"}, usage = "The name of the task to operate on.")
	private String newTaskName = null;

	public void setShowHelp(boolean showHelp) {
		this.showHelp = showHelp;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public void setObjectOperatingOn(KeeperObjectType objectOperatingOn) {
		this.objectOperatingOn = objectOperatingOn;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getNewTaskName() {
		return newTaskName;
	}

	public void setNewTaskName(String newTaskName) {
		this.newTaskName = newTaskName;
	}

	public Boolean getShowHelp() {
		return showHelp;
	}

	public Boolean getQuit() {
		return quit;
	}

	public Action getAction() {
		return action;
	}

	public KeeperObjectType getObjectOperatingOn() {
		return objectOperatingOn;
	}
}
