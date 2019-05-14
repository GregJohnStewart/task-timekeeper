package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.Timespan;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;

import java.util.List;

public class TimespanDoer extends ActionDoer<Timespan> {

	private PeriodDoer periodDoer;

	public TimespanDoer(PeriodDoer periodDoer){
		this.periodDoer = periodDoer;
	}


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

	@Override
	public List<Timespan> search(TimeManager manager, ActionConfig config) {
		return null;
	}

	@Override
	public List<String> getViewHeaders() {
		return null;
	}

	@Override
	public List<String> getViewRowEntries(int rowNum, Timespan object) {
		return null;
	}
}
