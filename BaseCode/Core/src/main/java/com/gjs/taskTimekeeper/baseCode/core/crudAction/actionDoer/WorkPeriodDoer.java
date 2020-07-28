package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;
import com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * Action doer to edit WorkPeriods.
 */
@Slf4j
public class WorkPeriodDoer extends CrudDoer<WorkPeriod> {
	
	/**
	 * The selected work period. If null, none are selected.
	 */
	protected WorkPeriod selected = null;
	
	public WorkPeriodDoer(TimeManager manager) {
		super(manager);
	}
	
	public WorkPeriodDoer(TimeManager manager, Outputter outputter) {
		super(manager, outputter);
	}
	
	/**
	 * Gets the currently selected work period.
	 *
	 * <p>The selected work period set will .equals() that in the time manager given but will be a
	 * different object between runs (assuming between runs the TimeManager is reloaded).
	 *
	 * @return The currently selected work period.
	 */
	protected WorkPeriod getSelected() {
		return selected;
	}
	
	/**
	 * Determines if the period given is selected.
	 *
	 * @param period The period to check if selected.
	 * @return
	 */
	public boolean isSelected(WorkPeriod period) {
		return this.selected != null && this.selected.equals(period);
	}
	
	/**
	 * Sets the selected work period.
	 *
	 * @param period The period to set as selected.
	 */
	protected void setSelected(WorkPeriod period) {
		this.selected = period;
	}
	
	@Override
	protected boolean add(ActionConfig config) {
		log.info("Adding a new work period.");
		WorkPeriod period = new WorkPeriod();
		
		if(config.getAttributeName() != null && config.getAttributes() != null) {
			log.warn("Cannot process both single attribute and set of attributes.");
			outputter.errorPrintln("Cannot process both single attribute and set of attributes.");
			return false;
		}
		
		if(config.getAttributeName() != null) {
			if(config.getAttributeVal() != null) {
				period.getAttributes().put(config.getAttributeName(), config.getAttributeVal());
			}
		} else {
			log.debug("No attributes to add.");
		}
		if(config.getAttributes() != null) {
			Map<String, String> newAtts;
			try {
				newAtts = ActionDoer.parseAttributes(config.getAttributes());
			} catch(IllegalArgumentException e) {
				log.warn("Attribute string given was invalid. Error: ", e);
				outputter.errorPrintln(
					"Attribute string given was invalid. Error: " + e.getMessage());
				return false;
			}
			period.setAttributes(newAtts);
		}
		
		int numBefore = manager.getWorkPeriods().size();
		manager.addWorkPeriod(period);
		
		if(numBefore == manager.getWorkPeriods().size()) {
			log.info("Empty period already exists, nothing happened.");
			outputter.errorPrintln("An empty period already exists.");
			return false;
		}
		outputter.normPrintln(OutputLevel.DEFAULT, "Added new work period.");
		
		if(config.getSelect()) {
			this.setSelected(period);
			outputter.normPrintln(OutputLevel.DEFAULT, "Selected the new work period.");
		}
		
		return true;
	}
	
	/**
	 * Edits the selected WorkPeriod
	 *
	 * @param config The configuration to provide details for the change.
	 * @return
	 */
	@Override
	protected boolean edit(ActionConfig config) {
		WorkPeriod period = this.getSelected();
		
		if(period == null) {
			log.warn("No period selected. Cannot edit it.");
			outputter.errorPrintln("No period selected to edit.");
			return false;
		}
		if(config.getAttributeName() != null && config.getAttributes() != null) {
			log.warn("Cannot process both single attribute and set of attributes.");
			outputter.errorPrintln("Cannot process both single attribute and set of attributes.");
			return false;
		}
		
		boolean modified = false;
		
		if(config.getAttributeName() != null) {
			if(config.getAttributeVal() != null) {
				String returned =
					period.getAttributes()
						  .put(config.getAttributeName(), config.getAttributeVal());
				if(!config.getAttributeVal().equals(returned)) {
					modified = true;
				}
			} else {
				boolean contained = period.getAttributes().containsKey(config.getAttributeName());
				period.getAttributes().remove(config.getAttributeName());
				modified = contained;
			}
		}
		if(config.getAttributes() != null) {
			Map<String, String> newAtts;
			try {
				newAtts = ActionDoer.parseAttributes(config.getAttributes());
			} catch(IllegalArgumentException e) {
				log.warn("Attribute string given was invalid. Error: ", e);
				outputter.errorPrintln(
					"Attribute string given was invalid. Error: " + e.getMessage());
				return false;
			}
			if(!period.getAttributes().equals(newAtts)) {
				period.setAttributes(newAtts);
				modified = true;
			} else {
				log.debug("Attribute map given same as what was already held.");
			}
		}
		
		if(modified) {
			outputter.normPrintln(OutputLevel.VERBOSE, "Period was modified.");
		} else {
			outputter.normPrintln(OutputLevel.VERBOSE, "Period was not modified.");
		}
		
		return modified;
	}
	
	protected void deselectIfRemoved() {
		log.info("Determining if selected period was removed.");
		WorkPeriod selected = this.getSelected();
		if(selected != null && !manager.getWorkPeriods().contains(selected)) {
			this.setSelected(null);
			log.info("Selected period was removed.");
		} else {
			log.info("Selected period was not removed.");
		}
	}
	
	@Override
	protected boolean remove(ActionConfig config) {
		if(manager.getWorkPeriods().isEmpty()) {
			log.warn("No period(s) to remove.");
			outputter.errorPrintln("No period(s) to remove.");
			return false;
		}
		boolean result = false;
		if(config.getIndex() != null) {
			result = this.removeOne(manager, config);
			this.deselectIfRemoved();
			return result;
		} else if(config.getBefore() != null || config.getAfter() != null) {
			result = this.removeBeforeAfter(manager, config);
			this.deselectIfRemoved();
			return result;
		}
		log.warn("No period(s) specified to remove.");
		outputter.errorPrintln("No period(s) specified to remove.");
		return false;
	}
	
	private boolean removeOne(TimeManager manager, ActionConfig config) {
		log.info("Removing one period.");
		outputter.normPrintln(OutputLevel.VERBOSE, "Removing a single period.");
		
		WorkPeriod period = this.getAtIndex(config);
		if(period == null) {
			log.warn("No work period at this index.");
			outputter.errorPrintln("No work period at this index.");
			return false;
		}
		
		manager.getWorkPeriods().remove(period);
		return true;
	}
	
	private boolean removeBeforeAfter(TimeManager manager, ActionConfig config) {
		log.info("Removing periods before or after given datetimes.");
		outputter.normPrintln(
			OutputLevel.DEFAULT, "Removing periods before or after given datetimes.");
		
		LocalDateTime before = null;
		if(config.getBefore() != null) {
			before = TimeParser.parse(config.getBefore());
			if(before == null) {
				log.warn(
					"Could not parse a datetime from before datetime. Erring datetime: \"{}\"",
					config.getBefore()
				);
				outputter.errorPrintln("No datetime could be parsed from before datetime.");
				return false;
			}
		}
		
		LocalDateTime after = null;
		if(config.getAfter() != null) {
			after = TimeParser.parse(config.getAfter());
			if(after == null) {
				log.warn(
					"Could not parse a datetime from after datetime. Erring datetime: \"{}\"",
					config.getAfter()
				);
				outputter.errorPrintln("No datetime could be parsed from after datetime.");
				return false;
			}
		}
		
		if(before != null && after != null && after.isAfter(before)) {
			log.warn("The before datetime was after the after datetime.");
			outputter.errorPrintln("The before datetime was after the after datetime.");
			return false;
		}
		
		Collection<WorkPeriod> periodsToKeep = new ArrayList<>();
		for(WorkPeriod period : manager.getWorkPeriods()) {
			if(before == null) {
				if(period.compareTo(after) <= 0) {
					periodsToKeep.add(period);
				}
			} else if(after == null) {
				if(period.compareTo(before) >= 0) {
					periodsToKeep.add(period);
				}
			} else {
				if(period.compareTo(before) >= 0 || period.compareTo(after) <= 0) {
					periodsToKeep.add(period);
				}
			}
		}
		
		int numRemoved = manager.getWorkPeriods().size() - periodsToKeep.size();
		
		log.debug("Removed {} periods.", numRemoved);
		outputter.normPrintln(OutputLevel.DEFAULT, "Removing " + numRemoved + " periods.");
		
		return manager.getWorkPeriods().retainAll(periodsToKeep);
	}
	
	@Override
	public void displayOne(WorkPeriod workPeriod) {
		
		outputter.normPrintln(OutputLevel.DEFAULT, "Period:");
		outputter.normPrintln(
			OutputLevel.DEFAULT,
			"\tStart: " + TimeParser.toOutputString(workPeriod.getStart())
		);
		outputter.normPrintln(
			OutputLevel.DEFAULT, "\t  End: " + TimeParser.toOutputString(workPeriod.getEnd()));
		outputter.normPrintln(
			OutputLevel.DEFAULT,
			"\tTotal time: " + TimeParser.toDurationString(workPeriod.getTotalTime())
		);
		outputter.normPrintln(
			OutputLevel.DEFAULT, "\tSelected: " + (this.isSelected(workPeriod) ? "Yes" : "No"));
		outputter.normPrintln(OutputLevel.DEFAULT, "\t# Spans: " + workPeriod.getNumTimespans());
		outputter.normPrintln(
			OutputLevel.DEFAULT, "\tComplete: " + (workPeriod.isUnCompleted() ? "No" : "Yes"));
		
		for(Map.Entry<String, String> att : workPeriod.getAttributes().entrySet()) {
			outputter.normPrintln(OutputLevel.DEFAULT, "\t" + att.getKey() + ": " + att.getValue());
		}
		
		outputter.normPrintln(OutputLevel.DEFAULT, "\tTime spent on tasks:");
		for(Name curTask : workPeriod.getTaskNames()) {
			outputter.normPrintln(
				OutputLevel.DEFAULT,
				"\t\t"
					+ curTask
					+ " "
					+ TimeParser.toDurationString(workPeriod.getTotalTimeWith(curTask))
			);
		}
	}
	
	@Override
	public void view(ActionConfig config) {
		log.info("Viewing periods.");
		if(config.getIndex() != null) {
			WorkPeriod result = this.getAtIndex(config);
			if(result == null) {
				log.warn("No result found at index.");
				outputter.errorPrintln("No result found at index.");
				return;
			}
			
			if(config.getSelect()) {
				log.info("Selecting new Work Period.");
				outputter.normPrintln(OutputLevel.DEFAULT, "Selecting the following period:");
				this.setSelected(result);
			}
			
			this.displayOne(result);
			
			return;
		}
		
		List<WorkPeriod> results = this.search(config);
		
		this.printView("Periods", results);
	}
	
	@Override
	public List<WorkPeriod> search(ActionConfig config) {
		List<WorkPeriod> results = new LinkedList<>(manager.getWorkPeriods());
		
		// remove results as necessary
		
		if(config.getBefore() != null) {
			LocalDateTime beforeThreshold = TimeParser.parse(config.getBefore());
			if(beforeThreshold == null) {
				log.error("Before threshold was a malformed datetime.");
				System.err.println("Before threshold was a malformed datetime.");
				return null;
			}
			for(WorkPeriod result : results) {
				if(result.getStart().isAfter(beforeThreshold)) {
					results.remove(result);
				}
			}
		}
		if(config.getAfter() != null) {
			LocalDateTime afterThreshold = TimeParser.parse(config.getAfter());
			if(afterThreshold == null) {
				log.error("After threshold was a malformed datetime.");
				System.err.println("After threshold was a malformed datetime.");
				return null;
			}
			for(WorkPeriod result : results) {
				if(result.getEnd().isBefore(afterThreshold)) {
					results.remove(result);
				}
			}
		}
		if(config.getAttributeName() != null) {
			for(WorkPeriod result : results) {
				if(result.getAttributes().containsKey(config.getAttributeName())) {
					// TODO:: if either value is null?
					// if result does not have att and value, remove
					if(!result.getAttributes()
							  .get(config.getAttributeName())
							  .equals(config.getAttributeVal())) {
						results.remove(result);
					}
				} else {
					// if result does not have attribute, but is supposed to
					if(config.getAttributeVal() != null) {
						results.remove(result);
					}
				}
			}
		}
		
		Collections.reverse(results);
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
		output.add("Complete?");
		output.add("Task Times");
		
		return output;
	}
	
	@Override
	public List<String> getViewRowEntries(int rowNum, WorkPeriod period) {
		List<String> output = new ArrayList<>();
		
		output.add("" + rowNum);
		output.add((this.isSelected(period) ? "*" : ""));
		
		output.add(TimeParser.toOutputString(period.getStart()));
		output.add(TimeParser.toOutputString(period.getEnd()));
		output.add(TimeParser.toDurationString(period.getTotalTime()));
		output.add(period.isUnCompleted() ? "No" : "Yes");
		
		SortedSet<Name> tasks = period.getTaskNames();
		
		if(tasks.isEmpty()) {
			output.add("");
		} else {
			StringBuilder sb = new StringBuilder();
			Iterator<Name> taskIt = tasks.iterator();
			
			while(taskIt.hasNext()) {
				Name curTask = taskIt.next();
				
				sb.append(curTask);
				sb.append(" (");
				sb.append(TimeParser.toDurationString(period.getTotalTimeWith(curTask)));
				sb.append(")");
				
				if(taskIt.hasNext()) {
					sb.append(" | ");
				}
			}
			output.add(sb.toString());
		}
		
		return output;
	}
}
