package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.Timespan;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The ActionDoer to handle Timespans.
 */
@Slf4j
public class TimespanDoer extends CrudDoer<Timespan> {
	/**
	 * The period doer also being used the ActionDoer to get the selected work period.
	 */
	private final WorkPeriodDoer workPeriodDoer;
	
	public TimespanDoer(WorkPeriodDoer workPeriodDoer, TimeManager manager)
		throws IllegalArgumentException {
		super(manager);
		this.workPeriodDoer = workPeriodDoer;
		if(this.manager != workPeriodDoer.manager) {
			throw new IllegalArgumentException(
				"Work period doer given had a different time manager than the one given.");
		}
	}
	
	public TimespanDoer(WorkPeriodDoer workPeriodDoer, TimeManager manager, Outputter outputter)
		throws IllegalArgumentException {
		super(manager, outputter);
		this.workPeriodDoer = workPeriodDoer;
		if(this.manager != workPeriodDoer.manager) {
			throw new IllegalArgumentException(
				"Work period doer given had a different time manager than the one given.");
		}
	}
	
	@Override
	protected boolean add(ActionConfig config) {
		WorkPeriod period = this.workPeriodDoer.getSelected();
		if(period == null) {
			log.error("No work period selected to add to.");
			outputter.errorPrintln("No work period selected to add to.");
			return false;
		}
		
		Timespan newSpan;
		{
			Task task;
			try {
				task = manager.getTaskByName(config.getName());
			} catch(Exception e) {
				log.error("Bad task name given. Error: ", e);
				outputter.errorPrintln("Error with task name given: " + e.getMessage());
				return false;
			}
			if(task == null) {
				log.error("No or invalid task name given.");
				outputter.errorPrintln("No or invalid task name given.");
				return false;
			}
			newSpan = new Timespan(task);
		}
		
		if(config.getStart() != null) {
			LocalDateTime start = TimeParser.parse(config.getStart());
			if(start == null) {
				log.error("Malformed starting datetime given.");
				outputter.errorPrintln("Malformed starting datetime given.");
				return false;
			}
			newSpan.setStartTime(start);
		}
		if(config.getEnd() != null) {
			LocalDateTime end = TimeParser.parse(config.getEnd());
			if(end == null) {
				log.error("Malformed starting datetime given.");
				outputter.errorPrintln("Malformed starting datetime given.");
				return false;
			}
			try {
				newSpan.setEndTime(end);
			} catch(IllegalArgumentException e) {
				log.error("End time cannot be before start time.");
				outputter.errorPrintln("End time cannot be before start time.");
				return false;
			}
		}
		
		boolean result = period.addTimespan(newSpan);
		if(!result) {
			log.warn("Timespan not added, was there already an empty one?");
			outputter.errorPrintln("Timespan not added. Was there already an empty one?");
		} else {
			outputter.normPrintln(OutputLevel.DEFAULT, "New timespan added.");
		}
		return result;
	}
	
	@Override
	protected boolean edit(ActionConfig config) {
		Timespan span = this.getAtIndex(config);
		if(span == null) {
			log.error("No span at given index.");
			outputter.errorPrintln("No span at given index.");
			return false;
		}
		
		LocalDateTime start = null;
		LocalDateTime end = null;
		
		if(config.getStart() != null) {
			start = TimeParser.parse(config.getStart());
			if(start == null) {
				log.error("Malformed starting datetime given.");
				outputter.errorPrintln("Malformed starting datetime given.");
				return false;
			}
		}
		if(config.getEnd() != null) {
			end = TimeParser.parse(config.getEnd());
			if(end == null) {
				log.error("Malformed ending datetime given.");
				outputter.errorPrintln("Malformed ending datetime given.");
				return false;
			}
		}
		
		Task newTask = null;
		if(config.getName() != null) {
			newTask = manager.getTaskByName(config.getName());
			if(newTask == null) {
				log.error("New task given does not exist in manager.");
				outputter.errorPrintln("New task given does not exist in manager");
				return false;
			}
		}
		
		boolean modified = false;
		if(start != null && end != null) {
			if(start.isAfter(end)) {
				log.error("Start given was after end given.");
				outputter.errorPrintln("Start given was after end given.");
				return false;
			}
			span.setEndTime(null);
			span.setStartTime(null);
			span.setStartTime(start);
			span.setEndTime(end);
			modified = true;
		} else if(start != null) {
			try {
				span.setStartTime(start);
				modified = true;
			} catch(IllegalArgumentException e) {
				log.error("Invalid start datetime given: ", e);
				outputter.errorPrintln(
					"Invalid start datetime given. Is it after the end datetime?");
				return false;
			}
		} else if(end != null) {
			try {
				span.setEndTime(end);
				modified = true;
			} catch(IllegalArgumentException e) {
				log.error("Invalid end datetime given: ", e);
				outputter.errorPrintln(
					"Invalid end datetime given. Is it before the start datetime?");
				return false;
			}
		}
		
		if(newTask != null) {
			span.setTaskName(newTask);
			modified = true;
		}
		
		if(modified) {
			outputter.normPrintln(OutputLevel.DEFAULT, "Timespan modified.");
		} else {
			outputter.normPrintln(OutputLevel.DEFAULT, "Timespan not modified.");
		}
		return modified;
	}
	
	@Override
	protected boolean remove(ActionConfig config) {
		Timespan span = this.getAtIndex(config);
		if(span == null) {
			log.error("No span at given index.");
			outputter.errorPrintln("No span at given index.");
			return false;
		}
		
		WorkPeriod period = this.workPeriodDoer.getSelected();
		
		period.getTimespans().remove(span);
		
		outputter.normPrintln(OutputLevel.DEFAULT, "Timespan removed.");
		// TODO:: add functionality for before/ after datetime, use search
		return true;
	}
	
	@Override
	public void displayOne(Timespan object) {
		// nothing to do, we do not need to do this for timespans
	}
	
	@Override
	public void view(ActionConfig config) {
		this.printView("Timespans in selected period", this.search(config));
	}
	
	@Override
	public List<Timespan> search(ActionConfig config) {
		WorkPeriod period = this.workPeriodDoer.getSelected();
		if(period == null) {
			log.error("No work period selected to search time spans in.");
			outputter.errorPrintln("No work period selected to search time spans in.");
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
