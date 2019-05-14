package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.WorkPeriod;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.timeParser.TimeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	 * Edits the selected WorkPeriod
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
		LOGGER.info("Viewing periods.");
		if(config.getIndex() != null){
			WorkPeriod result = this.getAtIndex(manager, config);
			if(result == null){
				LOGGER.warn("No result found at index.");
				System.err.println("No result found at index.");
				return;
			}

			if(config.isSelect()){
				LOGGER.info("Selecting new Work Period.");
				System.out.println("Selecting the following period:");
				this.setSelected(result);
			}

			System.out.println("Period:");
			System.out.println("\tStart:" + TimeParser.toOutputString(result.getStart()));
			System.out.println("\t  End:" + TimeParser.toOutputString(result.getEnd()));
			Duration totalTime = result.getTotalTime();
			System.out.println(totalTime.toHoursPart() + ":" + totalTime.toMinutesPart());
			System.out.println("\tSelected: " + (this.isSelected(result) ? "Yes" : "No"));
			System.out.println("\t# Spans: " + result.getNumTimespans());

			for(Map.Entry<String, String> att : result.getAttributes().entrySet()){
				System.out.println("\t"+ att.getKey() + ": " + att.getValue());
			}

			return;
		}

		List<WorkPeriod> results = this.search(manager, config);

		this.printView(results);
	}

	@Override
	public List<WorkPeriod> search(TimeManager manager, ActionConfig config) {
		List<WorkPeriod> results = new LinkedList<>(manager.getWorkPeriods());

		//remove results as necessary

		if(config.getBefore() != null){
			LocalDateTime beforeThreshold = TimeParser.parse(config.getBefore());
			if(beforeThreshold == null){
				LOGGER.error("Before threshold was a malformed datetime.");
				System.err.println("Before threshold was a malformed datetime.");
				return null;
			}
			for(WorkPeriod result : results){
				if(result.getStart().isAfter(beforeThreshold)){
					results.remove(result);
				}
			}
		}
		if(config.getAfter() != null){
			LocalDateTime afterThreshold = TimeParser.parse(config.getAfter());
			if(afterThreshold == null){
				LOGGER.error("After threshold was a malformed datetime.");
				System.err.println("After threshold was a malformed datetime.");
				return null;
			}
			for(WorkPeriod result : results){
				if(result.getEnd().isBefore(afterThreshold)){
					results.remove(result);
				}
			}
		}
		if(config.getAttributeName() != null){
			for(WorkPeriod result : results){
				if(result.getAttributes().containsKey(config.getAttributeName())){
					//TODO:: if either value is null?
					//if result does not have att and value, remove
					if(!result.getAttributes().get(config.getAttributeName()).equals(config.getAttributeVal())){
						results.remove(result);
					}
				}else{
					//if result does not have attribute, but is supposed to
					if(config.getAttributeVal() != null){
						results.remove(result);
					}
				}
			}
		}

		return results;
	}

	@Override
	public List<String> getViewHeaders() {
		List<String> output = new ArrayList<>();

		output.add("#");
		output.add("S");
		output.add("Start");
		output.add("End");
		output.add("Time Worked");

		return output;
	}

	@Override
	public List<String> getViewRowEntries(int rowNum, WorkPeriod period) {
		List<String> output = new ArrayList<>();

		output.add("" + rowNum);
		output.add(
			(this.isSelected(period) ? "*" : "")
		);

		output.add(TimeParser.toOutputString(period.getStart()));
		output.add(TimeParser.toOutputString(period.getEnd()));
		Duration totalTime = period.getTotalTime();
		output.add(totalTime.toHoursPart() + ":" + totalTime.toMinutesPart());

		return output;
	}
}
