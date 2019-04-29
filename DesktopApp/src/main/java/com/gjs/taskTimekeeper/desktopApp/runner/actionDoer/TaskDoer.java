package com.gjs.taskTimekeeper.desktopApp.runner.actionDoer;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.desktopApp.runner.CommandLine.CmdLineArgumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskDoer extends ActionDoer {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskDoer.class);

	@Override
	public boolean add(TimeManager manager, CmdLineArgumentParser parser) {
		return false;
	}

	@Override
	public boolean edit(TimeManager manager, CmdLineArgumentParser parser) {
		return false;
	}

	@Override
	public boolean remove(TimeManager manager, CmdLineArgumentParser parser) {
		return false;
	}

	@Override
	public void view(TimeManager manager, CmdLineArgumentParser parser) {
		LOGGER.info("Told to view one or more tasks.");


	}
}
