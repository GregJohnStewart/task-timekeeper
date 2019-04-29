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
	public abstract boolean add(TimeManager manager, CmdLineArgumentParser parser);

	/**
	 * Edits the data object.
	 * @param manager
	 * @param parser
	 * @return True if the object was changed, false if not.
	 */
	public abstract boolean edit(TimeManager manager, CmdLineArgumentParser parser);

	/**
	 * Removes the data object.
	 * @param manager
	 * @param parser
	 * @return True if the object was changed, false if not.
	 */
	public abstract boolean remove(TimeManager manager, CmdLineArgumentParser parser);

	/**
	 * Views the data object(s).
	 * @param manager
	 * @param parser
	 */
	public abstract void view(TimeManager manager, CmdLineArgumentParser parser);

	public static boolean doAction(TimeManager manager, CmdLineArgumentParser parser){
		return true;
	}
}
