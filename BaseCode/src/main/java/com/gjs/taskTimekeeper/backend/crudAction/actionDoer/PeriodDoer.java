package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.WorkPeriod;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * TODO:: implement a 'selected' period thing. Make ActionDoer keep track of actionDoers.
 */
public class PeriodDoer extends ActionDoer<WorkPeriod> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodDoer.class);

	protected WorkPeriod selected = null;

	protected WorkPeriod getSelected(){
		return selected;
	}

	public WorkPeriod getSelectedFromManager(TimeManager manager){
		for(WorkPeriod period : manager.getWorkPeriods()){
			if(period.equals(this.getSelected())){
				this.setSelected(period);
				return period;
			}
		}

		this.setSelected(null);
		return null;
	}

	protected boolean isSelected(WorkPeriod period){
		if(this.selected == null){
			return period == null;
		}
		return this.selected.equals(period);
	}

	protected void setSelected(WorkPeriod period){
		this.selected = period;
	}

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

		if(config.isSelect()){
			this.setSelected(period);
		}

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
		WorkPeriod period = this.getSelectedFromManager(manager);

		if(period == null){
			LOGGER.warn("No period selected. Cannot edit it.");
			System.err.println("No period selected.");
			return false;
		}

		boolean modified = false;

		if(config.getNewAttributeName() != null){
			if(config.getNewAttributeVal() != null){
				String returned = period.getAttributes().put(
					config.getNewAttributeName(),
					config.getAttributeVal()
				);
				if(!config.getNewAttributeVal().equals(returned)){
					modified = true;
				}
			}else{
				period.getAttributes().remove(config.getNewAttributeName());
				modified = true;
			}
		}

		return modified;
	}

	@Override
	protected boolean remove(TimeManager manager, ActionConfig config) {
		//TODO:: implement removing periods before certain times, etc. remove selected?
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
