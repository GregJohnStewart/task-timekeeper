package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.WorkPeriod;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;

import java.util.List;

public class PeriodDoer extends ActionDoer<WorkPeriod> {
	@Override
	protected boolean add(TimeManager manager, ActionConfig config) {
		WorkPeriod period = new WorkPeriod();

		if(config.getAttributeName() != null){
			if(config.getAttributeVal() != null){
				period.getAttributes().put(
					config.getAttributeName(),
					config.getAttributeVal()
				);
			}
			if(config.getNewAttributeVal() != null){
				period.getAttributes().put(
					config.getAttributeName(),
					config.getNewAttributeVal()
				);
			}
		}

		manager.addWorkPeriod(period);
		return true;
	}

	/**
	 *
	 * @param manager The manager being dealt with
	 * @param config The configuration to provide details for the change.
	 * @return
	 */
	@Override
	protected boolean edit(TimeManager manager, ActionConfig config) {
		WorkPeriod period = this.getAtIndex(manager, config);
		return false;
	}

	@Override
	protected boolean remove(TimeManager manager, ActionConfig config) {
		WorkPeriod period = this.getAtIndex(manager, config);

		if(period == null){
			return false;
		}

		manager.getWorkPeriods().remove(period);
		return true;
	}

	@Override
	public void view(TimeManager manager, ActionConfig config) {

	}

	@Override
	public List<WorkPeriod> search(TimeManager manager, ActionConfig config) {
		return null;
	}

	@Override
	public List<String> getViewHeaders() {
		return null;
	}

	@Override
	public List<String> getViewRowEntries(int rowNum, WorkPeriod object) {
		return null;
	}
}
