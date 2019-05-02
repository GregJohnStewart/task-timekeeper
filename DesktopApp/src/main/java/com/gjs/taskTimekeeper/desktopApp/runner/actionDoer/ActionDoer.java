package com.gjs.taskTimekeeper.desktopApp.runner.actionDoer;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.desktopApp.runner.CommandLine.CmdLineArgumentParser;

public abstract class ActionDoer {

	/**
	 * Adds the data object.
	 * @param manager
	 * @param parser
	 * @return True if the object was changed, false if not.
	 */
	protected abstract boolean add(TimeManager manager, CmdLineArgumentParser parser);

	/**
	 * Edits the data object.
	 * @param manager
	 * @param parser
	 * @return True if the object was changed, false if not.
	 */
	protected abstract boolean edit(TimeManager manager, CmdLineArgumentParser parser);

	/**
	 * Removes the data object.
	 * @param manager
	 * @param parser
	 * @return True if the object was changed, false if not.
	 */
	protected abstract boolean remove(TimeManager manager, CmdLineArgumentParser parser);

	/**
	 * Views the data object(s).
	 * @param manager
	 * @param parser
	 */
	public abstract void view(TimeManager manager, CmdLineArgumentParser parser);

	/**
	 * Does the action specified.
	 * @param manager
	 * @param parser
	 * @return
	 */
	public final boolean doAction(TimeManager manager, CmdLineArgumentParser parser){
		boolean changed = false;
		switch (parser.getAction()){
			case ADD:
				changed = this.add(manager, parser);
				break;
			case EDIT:
				changed = this.edit(manager, parser);
				break;
			case REMOVE:
				changed = this.remove(manager, parser);
				break;
			case VIEW:
				this.view(manager, parser);
				break;
			default:
				//TODO:: output nothing specified?
		}
		return changed;
	}

	/**
	 *
	 * @param manager
	 * @param parser
	 * @return
	 */
	public static boolean doObjAction(TimeManager manager, CmdLineArgumentParser parser){
		switch (parser.getObjectOperatingOn()){
			case TASK:
				new TaskDoer().doAction(manager, parser);
				break;
			case PERIOD:
				//TODO:: make doer
				break;
			case SPAN:
				//TODO:: make doer
				break;
		}
		return true;
	}
}
