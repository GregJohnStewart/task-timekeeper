package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.WorkPeriod;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.timeParser.TimeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Action doer to edit WorkPeriods.
 */
public class PeriodDoer extends ActionDoer<WorkPeriod> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodDoer.class);

	/** The selected work period. If null, none are selected. */
	protected WorkPeriod selected = null;

	/**
	 * Gets the currently selected work period.
	 *
	 * The selected work period set will .equals() that in the time manager given but will be a different object between runs (assuming between runs the TimeManager is reloaded).
	 *
	 * Use {@link #getSelectedFromManager(TimeManager)} to get the selected work period object that is part of the manager.
	 * @return The currently selected work period.
	 */
	protected WorkPeriod getSelected(){
		return selected;
	}

	/**
	 * Gets the work period from the time manager. Also resets the work period held to it, if found.
	 *
	 * If not found, nulls out the held selected object.
	 * @param manager The manager to get the selected work period from.
	 * @return The selected work period object from the TimeManager given. Null if not found.
	 */
	public WorkPeriod getSelectedFromManager(TimeManager manager){
		for(WorkPeriod period : manager.getWorkPeriods()){
			if(period.equals(this.getSelected())){
				this.setSelected(period);
				consolePrintln(OutputLevel.VERBOSE, "There was a selected work period.");
				return period;
			}
		}
		consolePrintln(OutputLevel.DEFAULT, "No period selected.");
		this.setSelected(null);
		return null;
	}

	/**
	 * Determines if the period given is selected.
	 * @param period The period to check if selected.
	 * @return
	 */
	protected boolean isSelected(WorkPeriod period){
		return this.selected != null && this.selected.equals(period);
	}

	/**
	 * Sets the selected work period.
	 * @param period The period to set as selected.
	 */
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
		}

		manager.addWorkPeriod(period);
		consolePrintln(OutputLevel.DEFAULT, "Added new work period.");

		if(config.isSelect()){
			this.setSelected(period);
			consolePrintln(OutputLevel.DEFAULT, "Selected the new work period.");
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
			consoleErrorPrintln("No period selected to edit.");
			return false;
		}

		boolean modified = false;

		if(config.getAttributeName() != null){
			if(config.getAttributeVal() != null){
				String returned = period.getAttributes().put(
					config.getAttributeName(),
					config.getAttributeVal()
				);
				if(!config.getAttributeVal().equals(returned)){
					modified = true;
				}
			}else{
				boolean contained = period.getAttributes().containsKey(config.getAttributeName());
				period.getAttributes().remove(config.getAttributeName());
				modified = contained;
			}
		}

		if(modified){
			consolePrintln(OutputLevel.VERBOSE, "Period was modified.");
		} else {
			consolePrintln(OutputLevel.VERBOSE, "Period was not modified.");
		}

		return modified;
	}

	@Override
	protected boolean remove(TimeManager manager, ActionConfig config) {
		if(manager.getWorkPeriods().isEmpty()){
			LOGGER.warn("No period(s) to remove.");
			consoleErrorPrintln("No period(s) to remove.");
			return false;
		}
		if(config.getIndex() != null){
			return this.removeOne(manager, config);
		}else if(config.getBefore() != null || config.getAfter() != null){
			return this.removeBeforeAfter(manager, config);
		}
		LOGGER.warn("No period(s) specified to remove.");
		consoleErrorPrintln("No period(s) specified to remove.");
		return false;
	}

	private boolean removeOne(TimeManager manager, ActionConfig config){
		LOGGER.info("Removing one period.");
		consolePrintln(OutputLevel.VERBOSE, "Removing a single period.");

		WorkPeriod period = this.getAtIndex(manager, config);
		if(period == null){
			LOGGER.warn("No work period at this index.");
			consoleErrorPrintln("No work period at this index.");
			return false;
		}

		manager.getWorkPeriods().remove(period);
		return true;
	}

	private boolean removeBeforeAfter(TimeManager manager, ActionConfig config){
		LOGGER.info("Removing periods before or after given datetimes.");
		System.out.println("Removing periods before or after given datetimes.");

		LocalDateTime before = null;
		if(config.getBefore() != null){
			before = TimeParser.parse(config.getBefore());
			if(before == null){
				LOGGER.warn("Could not parse a datetime from before datetime. Erring datetime: \"{}\"", config.getBefore());
				consoleErrorPrintln("No datetime could be parsed from before datetime.");
				return false;
			}
		}

		LocalDateTime after = null;
		if(config.getAfter() != null){
			after = TimeParser.parse(config.getAfter());
			if(after == null){
				LOGGER.warn("Could not parse a datetime from after datetime. Erring datetime: \"{}\"", config.getAfter());
				consoleErrorPrintln("No datetime could be parsed from after datetime.");
				return false;
			}
		}

		if(before != null && after != null && after.isAfter(before)){
			LOGGER.warn("The before datetime was after the after datetime.");
			consoleErrorPrintln("The before datetime was after the after datetime.");
			return false;
		}

		Collection<WorkPeriod> periodsToKeep = new ArrayList<>();
		for(WorkPeriod period : manager.getWorkPeriods()){
			if(before == null){
				if(period.compareTo(after) <= 0){
					periodsToKeep.add(period);
				}
			}else if(after == null){
				if(period.compareTo(before) >= 0){
					periodsToKeep.add(period);
				}
			} else {
				if(period.compareTo(before) >= 0 || period.compareTo(after) <= 0){
					periodsToKeep.add(period);
				}
			}
		}

		int numRemoved = manager.getWorkPeriods().size() - periodsToKeep.size();

		LOGGER.debug("Removed {} periods.", numRemoved);
		consolePrintln(OutputLevel.DEFAULT, "Removing " + numRemoved + " periods.");

		return manager.getWorkPeriods().retainAll(periodsToKeep);
	}

	@Override
	public void displayOne(TimeManager manager, WorkPeriod workPeriod) {

		System.out.println("Period:");
		System.out.println("\tStart: " + TimeParser.toOutputString(workPeriod.getStart()));
		System.out.println("\t  End: " + TimeParser.toOutputString(workPeriod.getEnd()));
		System.out.println("\tTotal time: " + TimeParser.toDurationString(workPeriod.getTotalTime()));
		System.out.println("\tSelected: " + (this.isSelected(workPeriod) ? "Yes" : "No"));
		System.out.println("\t# Spans: " + workPeriod.getNumTimespans());
		System.out.println("\tComplete: " + (workPeriod.isUnCompleted() ? "No" : "Yes"));

		for(Map.Entry<String, String> att : workPeriod.getAttributes().entrySet()){
			System.out.println("\t"+ att.getKey() + ": " + att.getValue());
		}

		System.out.println("\tTime spent on tasks:");
		for(Task curTask : workPeriod.getTasks()){
			Duration duration = workPeriod.getTotalTimeWith(curTask);
			System.out.println("\t\t" + curTask.getName() + " " + duration.toHoursPart() + ":" + duration.toMinutesPart());
		}
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

			this.displayOne(manager, result);

			return;
		}

		List<WorkPeriod> results = this.search(manager, config);

		this.printView("Periods", results);
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
		output.add(
			(this.isSelected(period) ? "*" : "")
		);

		output.add(TimeParser.toOutputString(period.getStart()));
		output.add(TimeParser.toOutputString(period.getEnd()));
		Duration totalTime = period.getTotalTime();
		output.add(totalTime.toHoursPart() + ":" + totalTime.toMinutesPart());
		output.add(period.isUnCompleted() ? "No" : "Yes");

		Set<Task> tasks = period.getTasks();

		if(tasks.isEmpty()) {
			output.add("");
		}else {
			StringBuilder sb = new StringBuilder();
			Iterator<Task> taskIt = tasks.iterator();

			while(taskIt.hasNext()){
				Task curTask = taskIt.next();

				Duration duration = period.getTotalTimeWith(curTask);

				sb.append(curTask.getName());
				sb.append(" (");
				sb.append(duration.toHoursPart());
				sb.append(':');
				sb.append(duration.toMinutesPart());
				sb.append(")");

				if(taskIt.hasNext()){
					sb.append(" | ");
				}
			}
			output.add(sb.toString());
		}

		return output;
	}
}
