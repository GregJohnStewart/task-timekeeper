package com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.Task;
import com.gjs.taskTimekeeper.baseCode.TimeManager;
import com.gjs.taskTimekeeper.baseCode.Timespan;
import com.gjs.taskTimekeeper.baseCode.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.timeParser.TimeParser;
import com.gjs.taskTimekeeper.baseCode.utils.OutputLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The ActionDoer to handle Timespans.
 */
public class TimespanDoer extends ActionDoer<Timespan> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimespanDoer.class);

	/**
	 * The period doer also being used the ActionDoer to get the selected work period.
	 */
	private final WorkPeriodDoer workPeriodDoer;

	public TimespanDoer(WorkPeriodDoer workPeriodDoer){
		this.workPeriodDoer = workPeriodDoer;
	}

	@Override
	protected boolean add(TimeManager manager, ActionConfig config) {
		WorkPeriod period = this.workPeriodDoer.getSelectedFromManager(manager);
		if(period == null){
			LOGGER.error("No work period selected to add to.");
			OUTPUTTER.errorPrintln("No work period selected to add to.");
			return false;
		}

		Timespan newSpan;
		{
			Task task;
			try {
				task = manager.getTaskByName(config.getName());
			}catch (Exception e){
				LOGGER.error("Bad task name given. Error: ", e);
				OUTPUTTER.errorPrintln("Error with task name given: " + e.getMessage());
				return false;
			}
			if(task == null){
				LOGGER.error("No or invalid task name given.");
				OUTPUTTER.errorPrintln("No or invalid task name given.");
				return false;
			}
			newSpan = new Timespan(task);
		}

		if(config.getStart() != null){
			LocalDateTime start = TimeParser.parse(config.getStart());
			if(start == null){
				LOGGER.error("Malformed starting datetime given.");
				OUTPUTTER.errorPrintln("Malformed starting datetime given.");
				return false;
			}
			newSpan.setStartTime(start);
		}
		if(config.getEnd() != null){
			LocalDateTime end = TimeParser.parse(config.getEnd());
			if(end == null){
				LOGGER.error("Malformed starting datetime given.");
				OUTPUTTER.errorPrintln("Malformed starting datetime given.");
				return false;
			}
			try {
				newSpan.setEndTime(end);
			}catch (IllegalArgumentException e){
				LOGGER.error("End time cannot be before start time.");
				OUTPUTTER.errorPrintln("End time cannot be before start time.");
				return false;
			}
		}

		boolean result = period.addTimespan(newSpan);
		if(!result){
			LOGGER.warn("Timespan not added, was there already an empty one?");
			OUTPUTTER.errorPrintln("Timespan not added. Was there already an empty one?");
		}else {
			OUTPUTTER.normPrintln(OutputLevel.DEFAULT, "New timespan added.");
		}
		return result;
	}

	@Override
	protected boolean edit(TimeManager manager, ActionConfig config) {
		Timespan span = this.getAtIndex(manager, config);
		if(span == null){
			LOGGER.error("No span at given index.");
			OUTPUTTER.errorPrintln("No span at given index.");
			return false;
		}

		LocalDateTime start = null;
		LocalDateTime end = null;

		if(config.getStart() != null){
			start = TimeParser.parse(config.getStart());
			if(start == null){
				LOGGER.error("Malformed starting datetime given.");
				OUTPUTTER.errorPrintln("Malformed starting datetime given.");
				return false;
			}
		}
		if(config.getEnd() != null){
			end = TimeParser.parse(config.getEnd());
			if(end == null){
				LOGGER.error("Malformed ending datetime given.");
				OUTPUTTER.errorPrintln("Malformed ending datetime given.");
				return false;
			}
		}

		Task newTask = null;
		if(config.getName() != null){
			newTask = manager.getTaskByName(config.getName());
			if(newTask == null){
				LOGGER.error("New task given does not exist in manager.");
				OUTPUTTER.errorPrintln("New task given does not exist in manager");
				return false;
			}
		}

		boolean modified = false;
		if(start != null && end != null){
			if(start.isAfter(end)){
				LOGGER.error("Start given was after end given.");
				OUTPUTTER.errorPrintln("Start given was after end given.");
				return false;
			}
			span.setEndTime(null);
			span.setStartTime(null);
			span.setStartTime(start);
			span.setEndTime(end);
			modified = true;
		} else if(start != null){
			try {
				span.setStartTime(start);
				modified = true;
			} catch (IllegalArgumentException e) {
				LOGGER.error("Invalid start datetime given: ", e);
				OUTPUTTER.errorPrintln("Invalid start datetime given. Is it after the end datetime?");
				return false;
			}
		} else if(end != null){
			try {
				span.setEndTime(end);
				modified = true;
			} catch (IllegalArgumentException e) {
				LOGGER.error("Invalid end datetime given: ", e);
				OUTPUTTER.errorPrintln("Invalid end datetime given. Is it before the start datetime?");
				return false;
			}
		}

		if(newTask != null){
			span.setTaskName(newTask);
			modified = true;
		}

		if(modified){
			OUTPUTTER.normPrintln(OutputLevel.DEFAULT, "Timespan modified.");
		} else {
			OUTPUTTER.normPrintln(OutputLevel.DEFAULT, "Timespan not modified.");
		}
		return modified;
	}

	@Override
	protected boolean remove(TimeManager manager, ActionConfig config) {
		Timespan span = this.getAtIndex(manager, config);
		if(span == null){
			LOGGER.error("No span at given index.");
			OUTPUTTER.errorPrintln("No span at given index.");
			return false;
		}

		WorkPeriod period = this.workPeriodDoer.getSelectedFromManager(manager);

		period.getTimespans().remove(span);

		OUTPUTTER.normPrintln(OutputLevel.DEFAULT, "Timespan removed.");
		//TODO:: add functionality for before/ after datetime?
		return true;
	}

	@Override
	public void displayOne(TimeManager manager, Timespan object) {
		//nothing to do, we do not need to do this for timespans
	}

	@Override
	public void view(TimeManager manager, ActionConfig config) {
		this.printView("Timespans in selected period", this.search(manager, config));
	}

	@Override
	public List<Timespan> search(TimeManager manager, ActionConfig config) {
		WorkPeriod period = this.workPeriodDoer.getSelectedFromManager(manager);
		if(period == null){
			LOGGER.error("No work period selected to search time spans in.");
			OUTPUTTER.errorPrintln("No work period selected to search time spans in.");
			return null;
		}

		return new LinkedList<>(period.getTimespans());
	}

	@Override
	public List<String> getViewHeaders() {
		List<String> output = new ArrayList<>();

		output.add("#");
		output.add("Start");
		output.add("End");
		output.add("Time");
		output.add("Task");

		return output;
	}

	@Override
	public List<String> getViewRowEntries(int rowNum, Timespan timespan) {
		List<String> output = new ArrayList<>();

		output.add("" + rowNum);

		output.add(TimeParser.toOutputString(timespan.getStartTime()));
		output.add(TimeParser.toOutputString(timespan.getEndTime()));
		output.add(TimeParser.toDurationString(timespan.getDuration()));
		output.add(timespan.getTaskName().toString());

		return output;
	}
}
