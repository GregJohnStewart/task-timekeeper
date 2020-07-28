package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.Action;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.Timespan;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;
import com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.gjs.taskTimekeeper.baseCode.core.crudAction.Action.ADD;
import static com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel.DEFAULT;

/**
 * Handles doing the special tasks
 */
@Slf4j
public class SpecialDoer extends ActionDoer {
	private final CrudOperator operator;
	
	public SpecialDoer(CrudOperator operator) {
		super(operator.getManager());
		this.operator = operator;
	}
	
	public SpecialDoer(CrudOperator operator, Outputter outputter) {
		super(operator.getManager(), outputter);
		this.operator = operator;
	}
	
	/**
	 * Processes special commands from the command line.
	 *
	 * @param config The configuration from command line
	 * @return If the manager was changed in any way.
	 */
	public boolean processSpecial(ActionConfig config) {
		switch(config.getSpecialAction().toLowerCase()) {
		case "completespans": {
			return this.completeSpansInSelected(config.getIndex());
		}
		case "newspan":
			return this.completeOldSpansAndAddNewInSelected(config);
		case "clearallmanagerdata":
			return this.clearAllData();
		case "selectnewest": {
			if(manager.getWorkPeriods().isEmpty()) {
				log.warn("No periods to select.");
				outputter.errorPrintln("No periods to select.");
				return false;
			}
			ActionConfig actionConfig =
				new ActionConfig()
					.setObjectOperatingOn(KeeperObjectType.PERIOD)
					.setAction(Action.VIEW)
					.setIndex(1)
					.setSelect(true);
			return this.operator.doObjAction(actionConfig);
		}
		case "newperiod": {
			boolean result = this.completeSpansInSelected(null);
			ActionConfig actionConfig =
				new ActionConfig()
					.setObjectOperatingOn(KeeperObjectType.PERIOD)
					.setAction(ADD)
					.setSelect(true);
			return this.operator.doObjAction(actionConfig) || result;
		}
		// TODO:: "lastWeeksPeriods"
		// TODO:: "thisWeeksPeriods"
		// TODO:: clearPeriods
		// TODO:: cleanTasks
		// TODO:: taskStats -> view amount of time spent on what tasks in a period.
		default:
			log.error("No valid special command given.");
			this.outputter.errorPrintln("No valid special command given.");
			return false;
		}
	}
	
	/**
	 * Finishes all spans in selected work period.
	 *
	 * @return True if any spans were actually finished.
	 */
	private boolean completeSpansInSelected(Integer index) {
		WorkPeriod selected = this.operator.getSelectedWorkPeriod();
		if(selected == null) {
			log.error("No work period selected.");
			this.outputter.errorPrintln("No work period selected.");
			return false;
		}
		
		if(selected.isCompleted()) {
			return false;
		}
		LocalDateTime now = LocalDateTime.now();
		if(index != null) {
			log.debug("Attempting to finish span at index {}", index);
			
			
			if(index <= 0 || index > selected.getTimespans().size()) {
				this.outputter.errorPrintln("Index given out of bounds.");
				return false;
			}
			
			Timespan span = null;
			int cur = 1;
			for(Timespan curSpan : selected.getTimespans()) {
				if(cur == index) {
					span = curSpan;
					break;
				}
				cur++;
			}
			
			//this probably shouldn't happen
			if(span == null) {
				this.outputter.errorPrintln("Not able to find timespan to complete.");
				return false;
			}
			
			return completeSpan(span, now);
		} else {
			log.debug("Attempting to finish spans in selected period.");
			this.outputter.normPrintln(DEFAULT, "Attempting to finish spans in selected period.");
			int finishedCount = 0;
			for(Timespan span : selected.getUnfinishedTimespans()) {
				if(completeSpan(span, now)) {
					finishedCount++;
				}
			}
			if(finishedCount > 0) {
				log.info("Finished {} spans.", finishedCount);
				this.outputter.normPrintln(DEFAULT, "Finished " + finishedCount + " spans.");
				return true;
			}
		}
		return false;
	}
	
	private static boolean completeSpan(Timespan span, LocalDateTime now) {
		if(!span.hasStartTime()) {
			return false;
		}
		if(!span.hasEndTime()) {
			if(span.getStartTime().isAfter(now)) {
				span.setEndTime(span.getStartTime().plusSeconds(1));
			} else {
				span.setEndTime(LocalDateTime.now());
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Finishes all old spans in selected work period and starts a new work period.
	 *
	 * @param config The configuration used to do the action.
	 * @return If the manager was changed during the operation.
	 */
	private boolean completeOldSpansAndAddNewInSelected(ActionConfig config) {
		log.info("Setting up config for adding a span.");
		WorkPeriod selected = this.operator.getSelectedWorkPeriod();
		if(selected == null) {
			log.error("No work period selected.");
			outputter.errorPrintln("No work period selected.");
			return false;
		}
		Name taskName;
		try {
			taskName = new Name(config.getName());
		} catch(Exception e) {
			log.error("Bad task name given: ", e);
			outputter.errorPrintln("Bad task name given: " + e.getMessage());
			return false;
		}
		Task task = manager.getTaskByName(taskName);
		if(task == null) {
			log.error("No task with name specified.");
			outputter.errorPrintln("No task with name specified.");
			return false;
		}
		// finish unfinished spans
		this.completeSpansInSelected(null);
		
		selected.addTimespan(new Timespan(task, LocalDateTime.now()));
		outputter.normPrintln(DEFAULT, "Added new timespan after finishing the existing ones.");
		return true;
	}
	
	private boolean clearAllData() {
		log.info("Clearing ALL data from manager.");
		outputter.normPrintln(DEFAULT, "Clearing ALL data from manager.");
		boolean output = false;
		
		if(!this.manager.getWorkPeriods().isEmpty()) {
			outputter.normPrintln(
				OutputLevel.VERBOSE,
				"Removing " + this.manager.getWorkPeriods().size() + " work periods."
			);
			output = true;
			this.manager.getWorkPeriods().removeIf(WorkPeriod->{
				return true;
			});
		}
		if(!this.manager.getTasks().isEmpty()) {
			output = true;
			outputter.normPrintln(OutputLevel.VERBOSE, "Removing " + this.manager.getTasks().size() + " tasks.");
			this.manager.getTasks().removeIf(Task->{
				return true;
			});
		}
		
		return output;
	}
}
