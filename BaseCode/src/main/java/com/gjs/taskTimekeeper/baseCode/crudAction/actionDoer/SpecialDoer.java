package com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.Task;
import com.gjs.taskTimekeeper.baseCode.TimeManager;
import com.gjs.taskTimekeeper.baseCode.Timespan;
import com.gjs.taskTimekeeper.baseCode.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.crudAction.Action;
import com.gjs.taskTimekeeper.baseCode.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.utils.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static com.gjs.taskTimekeeper.baseCode.crudAction.Action.ADD;
import static com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer.ActionDoer.OUTPUTTER;
import static com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer.ActionDoer.doObjAction;
import static com.gjs.taskTimekeeper.baseCode.utils.OutputLevel.DEFAULT;

/**
 * Handles doing the special tasks
 */
public class SpecialDoer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpecialDoer.class);

	/**
	 * Processes special commands from the command line.
	 * @param manager The manager being dealt with
	 * @param config The configuration from command line
	 * @return If the manager was changed in any way.
	 */
	public static boolean processSpecial(TimeManager manager, ActionConfig config){
		switch (config.getSpecialAction().toLowerCase()){
			case "completespans": {
				return completeSpansInSelected(manager);
			}
			case "newspan":
				return completeOldSpansAndAddNewInSelected(manager, config);
			case "selectnewest": {
				if(manager.getWorkPeriods().isEmpty()){
					LOGGER.warn("No periods to select.");
					OUTPUTTER.errorPrintln("No periods to select.");
					return false;
				}
				ActionConfig actionConfig = new ActionConfig()
					                            .setObjectOperatingOn(KeeperObjectType.PERIOD)
					                            .setAction(Action.VIEW)
					                            .setIndex(1)
					                            .setSelect(true);
				return doObjAction(manager, actionConfig);
			}
			case "newperiod": {
				boolean result = completeSpansInSelected(manager);
				ActionConfig actionConfig = new ActionConfig()
					                            .setObjectOperatingOn(KeeperObjectType.PERIOD)
					                            .setAction(ADD)
					                            .setSelect(true);
				return doObjAction(manager, actionConfig) || result;
			}
			//TODO:: "lastWeeksPeriods"
			//TODO:: "thisWeeksPeriods"
			//TODO:: clearPeriods
			//TODO:: clearAll
			//TODO:: cleanTasks
			//TODO:: taskStats -> view amount of time spent on what tasks in a period.
			default:
				LOGGER.error("No valid special command given.");
				OUTPUTTER.errorPrintln("No valid special command given.");
				return false;
		}
	}

	/**
	 * Finishes all spans in selected work period.
	 * @param manager The manager being dealt with.
	 * @return True if any spans were actually finished.
	 */
	private static boolean completeSpansInSelected(TimeManager manager){
		WorkPeriod selected = ActionDoer.getSelectedWorkPeriod();
		if(selected == null){
			LOGGER.error("No work period selected.");
			OUTPUTTER.errorPrintln("No work period selected.");
			return false;
		}

		if(selected.isUnCompleted()){
			LOGGER.debug("Attempting to finish spans in selected periods.");
			OUTPUTTER.normPrintln(DEFAULT, "Attempting to finish spans in selected periods.");
			int finishedCount = 0;
			LocalDateTime now = LocalDateTime.now();
			for(Timespan span : selected.getUnfinishedTimespans()){
				if(!span.hasStartTime()){
					continue;
				}
				if(!span.hasEndTime()){
					if(span.getStartTime().isAfter(now)){
						span.setEndTime(
							span.getStartTime().plusSeconds(1)
						);
					} else {
						span.setEndTime(LocalDateTime.now());
					}

					finishedCount++;
				}
			}
			if(finishedCount > 0){
				LOGGER.info("Finished {} spans.", finishedCount);
				OUTPUTTER.normPrintln(DEFAULT, "Finished " + finishedCount + " spans.");
				return true;
			}
		}
		return false;
	}

	/**
	 * Finishes all old spans in selected work period and starts a new work period.
	 * @param manager The time manager being dealt with.
	 * @param config The configuration used to do the action.
	 * @return If the manager was changed during the operation.
	 */
	private static boolean completeOldSpansAndAddNewInSelected(TimeManager manager, ActionConfig config){
		LOGGER.info("Setting up config for adding a span.");
		WorkPeriod selected = ActionDoer.getSelectedWorkPeriod();
		if(selected == null){
			LOGGER.error("No work period selected.");
			OUTPUTTER.errorPrintln("No work period selected.");
			return false;
		}
		Name taskName;
		try{
			taskName = new Name(config.getName());
		} catch (Exception e) {
			LOGGER.error("Bad task name given: ", e);
			OUTPUTTER.errorPrintln("Bad task name given: " + e.getMessage());
			return false;
		}
		Task task = manager.getTaskByName(taskName);
		if(task == null){
			LOGGER.error("No task with name specified.");
			OUTPUTTER.errorPrintln("No task with name specified.");
			return false;
		}
		//finish unfinished spans
		completeSpansInSelected(manager);

		selected.addTimespan(new Timespan(task, LocalDateTime.now()));
		OUTPUTTER.normPrintln(DEFAULT, "Added new timespan after finishing the existing ones.");
		return true;
	}

}
