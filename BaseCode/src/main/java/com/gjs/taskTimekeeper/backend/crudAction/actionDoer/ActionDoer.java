package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;

public abstract class ActionDoer {

	protected abstract boolean add(TimeManager manager, ActionConfig config);

	protected abstract boolean edit(TimeManager manager, ActionConfig config);

	protected abstract boolean remove(TimeManager manager, ActionConfig config);

	public abstract void view(TimeManager manager, ActionConfig config);

	public final boolean doAction(TimeManager manager, ActionConfig config){
		boolean changed = false;
		switch (config.getAction()){
			case ADD:
				changed = this.add(manager, config);
				break;
			case EDIT:
				changed = this.edit(manager, config);
				break;
			case REMOVE:
				changed = this.remove(manager, config);
				break;
			case VIEW:
				this.view(manager, config);
				break;
			default:
				//TODO:: output nothing specified?
		}
		return changed;
	}

	public static boolean doObjAction(TimeManager manager, ActionConfig config){
		switch (config.getObjectOperatingOn()){
			case TASK:
				new TaskDoer().doAction(manager, config);
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
