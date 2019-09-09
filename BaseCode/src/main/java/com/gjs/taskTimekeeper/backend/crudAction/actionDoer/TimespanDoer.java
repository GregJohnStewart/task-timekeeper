package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.Timespan;
import com.gjs.taskTimekeeper.backend.WorkPeriod;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.timeParser.TimeParser;
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
	private final PeriodDoer periodDoer;

	public TimespanDoer(PeriodDoer periodDoer){
		this.periodDoer = periodDoer;
	}

	@Override
	protected boolean add(TimeManager manager, ActionConfig config) {
		WorkPeriod period = this.periodDoer.getSelectedFromManager(manager);
		if(period == null){
			LOGGER.error("No work period selected to add to.");
			consoleErrorPrintln("No work period selected to add to.");
			return false;
		}

		Timespan newSpan;
		{
			Task task = manager.getTaskByName(config.getName());
			if(task == null){
				LOGGER.error("No or invalid task name given.");
				consoleErrorPrintln("No or invalid task name given.");
				return false;
			}
			newSpan = new Timespan(task);
		}

		if(config.getStart() != null){
			LocalDateTime start = TimeParser.parse(config.getStart());
			if(start == null){
				LOGGER.error("Malformed starting datetime given.");
				consoleErrorPrintln("Malformed starting datetime given.");
				return false;
			}
			newSpan.setStartTime(start);
		}
		if(config.getEnd() != null){
			LocalDateTime end = TimeParser.parse(config.getEnd());
			if(end == null){
				LOGGER.error("Malformed starting datetime given.");
				consoleErrorPrintln("Malformed starting datetime given.");
				return false;
			}
			try {
				newSpan.setEndTime(end);
			}catch (IllegalArgumentException e){
				LOGGER.error("End time cannot be before start time.");
				consoleErrorPrintln("End time cannot be before start time.");
				return false;
			}
		}

		consolePrintln(OutputLevel.DEFAULT, "New timespan added.");
		return period.addTimespan(newSpan);
	}

	@Override
	protected boolean edit(TimeManager manager, ActionConfig config) {
		Timespan span = this.getAtIndex(manager, config);
		if(span == null){
			LOGGER.error("No span at given index.");
			consoleErrorPrintln("No span at given index.");
			return false;
		}

		LocalDateTime start = null;
		LocalDateTime end = null;

		if(config.getStart() != null){
			start = TimeParser.parse(config.getStart());
			if(start == null){
				LOGGER.error("Malformed starting datetime given.");
				consoleErrorPrintln("Malformed starting datetime given.");
				return false;
			}
		}
		if(config.getEnd() != null){
			end = TimeParser.parse(config.getEnd());
			if(end == null){
				LOGGER.error("Malformed ending datetime given.");
				consoleErrorPrintln("Malformed ending datetime given.");
				return false;
			}
		}

		Task newTask = null;
		if(config.getName() != null){
			newTask = manager.getTaskByName(config.getName());
			if(newTask == null){
				LOGGER.error("New task given does not exist in manager.");
				consoleErrorPrintln("New task given does not exist in manager");
				return false;
			}
		}

		boolean modified = false;
		if(start != null && end != null){
			if(start.isAfter(end)){
				LOGGER.error("Start given was after end given.");
				consoleErrorPrintln("Start given was after end given.");
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
				consoleErrorPrintln("Invalid start datetime given. Is it after the end datetime?");
				return false;
			}
		} else if(end != null){
			try {
				span.setEndTime(end);
				modified = true;
			} catch (IllegalArgumentException e) {
				LOGGER.error("Invalid end datetime given: ", e);
				consoleErrorPrintln("Invalid end datetime given. Is it before the start datetime?");
				return false;
			}
		}

		if(newTask != null){
			span.setTask(newTask);
			modified = true;
		}

		if(modified){
			consolePrintln(OutputLevel.DEFAULT, "Timespan modified.");
		} else {
			consolePrintln(OutputLevel.DEFAULT, "Timespan not modified.");
		}
		return modified;
	}

	@Override
	protected boolean remove(TimeManager manager, ActionConfig config) {
		Timespan span = this.getAtIndex(manager, config);
		if(span == null){
			LOGGER.error("No span at given index.");
			consoleErrorPrintln("No span at given index.");
			return false;
		}

		WorkPeriod period = this.periodDoer.getSelectedFromManager(manager);

		period.getTimespans().remove(span);

		consolePrintln(OutputLevel.DEFAULT, "Timespan removed.");
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
		WorkPeriod period = this.periodDoer.getSelectedFromManager(manager);
		if(period == null){
			LOGGER.error("No work period selected to search time spans in.");
			consoleErrorPrintln("No work period selected to search time spans in.");
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
		output.add(timespan.getTask().getName());

		return output;
	}
}
