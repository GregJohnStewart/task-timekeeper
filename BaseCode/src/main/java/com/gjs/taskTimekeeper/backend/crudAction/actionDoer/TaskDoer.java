package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;


import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskDoer extends ActionDoer {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskDoer.class);

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
