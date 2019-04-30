package com.gjs.taskTimekeeper.desktopApp.runner.actionDoer;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.desktopApp.runner.CommandLine.CmdLineArgumentParser;

public class TimeSpanDoer extends ActionDoer {
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

	}
}
