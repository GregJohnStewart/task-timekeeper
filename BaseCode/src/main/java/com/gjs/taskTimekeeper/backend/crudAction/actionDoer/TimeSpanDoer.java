package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;


import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;

public class TimeSpanDoer extends ActionDoer {
	@Override
	protected boolean add(TimeManager manager, ActionConfig config) {
		return false;
	}

	@Override
	protected boolean edit(TimeManager manager, ActionConfig config) {
		return false;
	}

	@Override
	protected boolean remove(TimeManager manager, ActionConfig config) {
		return false;
	}

	@Override
	public void view(TimeManager manager, ActionConfig config) {

	}
}
