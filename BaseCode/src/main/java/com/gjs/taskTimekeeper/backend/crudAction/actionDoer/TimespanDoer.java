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

public class TimespanDoer extends ActionDoer<Timespan> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimespanDoer.class);

	private PeriodDoer periodDoer;

	public TimespanDoer(PeriodDoer periodDoer){
		this.periodDoer = periodDoer;
	}

	@Override
	protected boolean add(TimeManager manager, ActionConfig config) {
		WorkPeriod period = this.periodDoer.getSelectedFromManager(manager);
		if(period == null){
			LOGGER.error("No work period selected to add to.");
			System.err.println("No work period selected to add to.");
			return false;
		}

		Timespan newSpan;
		{
			Task task = manager.getTaskByName(config.getName());
			if(task == null){
				LOGGER.error("No or invalid task name given.");
				System.err.println("No or invalid task name given.");
				return false;
			}
			newSpan = new Timespan(task);
		}

		if(config.getAfter() != null){
			LocalDateTime start = TimeParser.parse(config.getAfter());
			if(start == null){
				LOGGER.error("Malformed starting datetime given.");
				System.err.println("Malformed starting datetime given.");
				return false;
			}
			newSpan.setStartTime(start);
		}
		if(config.getBefore() != null){
			LocalDateTime end = TimeParser.parse(config.getBefore());
			if(end == null){
				LOGGER.error("Malformed starting datetime given.");
				System.err.println("Malformed starting datetime given.");
				return false;
			}
			newSpan.setEndTime(end);
		}

		return period.addTimespan(newSpan);
	}

	@Override
	protected boolean edit(TimeManager manager, ActionConfig config) {
		Timespan span = this.getAtIndex(manager, config);
		if(span == null){
			LOGGER.error("No span at given index.");
			System.err.println("No span at given index.");
			return false;
		}

		boolean modified = false;

		if(config.getAfter() != null){
			LocalDateTime start = TimeParser.parse(config.getAfter());
			if(start == null){
				LOGGER.error("Malformed starting datetime given.");
				System.err.println("Malformed starting datetime given.");
				return false;
			}
			span.setStartTime(start);
			modified = true;
		}
		if(config.getBefore() != null){
			LocalDateTime end = TimeParser.parse(config.getBefore());
			if(end == null){
				LOGGER.error("Malformed starting datetime given.");
				System.err.println("Malformed starting datetime given.");
				return false;
			}
			span.setEndTime(end);
			modified = true;
		}
		if(config.getName() != null){
			Task newTask = manager.getTaskByName(config.getName());
			if(newTask == null){
				LOGGER.error("New task given does not exist in manager.");
				System.err.println("New task given does not exist in manager");
				return false;
			}
		}

		return modified;
	}

	@Override
	protected boolean remove(TimeManager manager, ActionConfig config) {
		Timespan span = this.getAtIndex(manager, config);
		if(span == null){
			LOGGER.error("No span at given index.");
			System.err.println("No span at given index.");
			return false;
		}

		WorkPeriod period = this.periodDoer.getSelectedFromManager(manager);

		period.getTimespans().remove(span);
		return true;
	}

	@Override
	public void displayOne(TimeManager manager, Timespan object) {
		//nothing to do, we do not need to do this for tasks
	}

	@Override
	public void view(TimeManager manager, ActionConfig config) {
		this.printView(this.search(manager, config));
	}

	@Override
	public List<Timespan> search(TimeManager manager, ActionConfig config) {
		WorkPeriod period = this.periodDoer.getSelectedFromManager(manager);
		if(period == null){
			LOGGER.error("No work period selected to search time spans in.");
			System.err.println("No work period selected to search time spans in.");
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
