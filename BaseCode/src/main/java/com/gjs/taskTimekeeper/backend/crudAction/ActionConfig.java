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
	private KeeperObject objectOperatingOn;

	//Identifiers/ data inputs to add:
	//  current (for periods/ timespans)
	//  name
	//  key/value pair
	//  time/date spans and instants

	@Option(name="-t", aliases = {"t", "--task", "task"}, usage = "The name of the task to operate on.")
	private String taskname = null;

	@Option(name="")

	public void setShowHelp(boolean showHelp) {
		this.showHelp = showHelp;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public void setObjectOperatingOn(KeeperObject objectOperatingOn) {
		this.objectOperatingOn = objectOperatingOn;
	}

	public String getTaskname() {
		return taskname;
	}

	public void setTaskname(String taskname) {
		this.taskname = taskname;
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

	public KeeperObject getObjectOperatingOn() {
		return objectOperatingOn;
	}
}
