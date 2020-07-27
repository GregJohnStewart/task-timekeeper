package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import lombok.extern.slf4j.Slf4j;

import static com.gjs.taskTimekeeper.baseCode.core.utils.OutputLevel.DEFAULT;

@Slf4j
public class CrudOperator {
	
	/**
	 * The outputter to use.
	 */
	private Outputter outputter = new Outputter();
	/**
	 * The time manager used.
	 */
	private final TimeManager manager;
	/**
	 * An instance of a task doer.
	 */
	private TaskDoer taskDoer;
	/**
	 * An instance of a period doer.
	 */
	private WorkPeriodDoer workPeriodDoer;
	/**
	 * An instance of a timespan doer.
	 */
	private TimespanDoer timespanDoer;
	/**
	 * An instance of a special doer
	 */
	private SpecialDoer specialDoer;
	/**
	 * If the doers have already been setup.
	 */
	private boolean doersSetup = false;
	
	public CrudOperator(TimeManager manager) {
		this.manager = manager;
		setupDoers();
	}
	
	public CrudOperator(TimeManager manager, Outputter outputter) {
		this(manager);
		this.setOutputter(outputter);
	}
	
	public void setOutputLevelThreshold(OutputLevel outputLevel) {
		this.outputter.setOutputLevelThreshold(outputLevel);
	}
	
	public Outputter getOutputter() {
		return this.outputter;
	}
	
	public void setOutputter(Outputter outputter) {
		if(outputter == null) {
			throw new IllegalArgumentException(
				"Outputter cannot be null. To disable output, set verbosity to NONE.");
		}
		this.outputter = outputter;
		this.resetDoerOutputters();
	}
	
	private void resetDoerOutputters() {
		this.taskDoer.setOutputter(this.outputter);
		this.workPeriodDoer.setOutputter(this.outputter);
		this.timespanDoer.setOutputter(this.outputter);
		this.specialDoer.setOutputter(this.outputter);
	}
	
	public TimeManager getManager() {
		return this.manager;
	}
	
	public TaskDoer getTaskDoer() {
		return taskDoer;
	}
	
	public WorkPeriodDoer getWorkPeriodDoer() {
		return workPeriodDoer;
	}
	
	public TimespanDoer getTimespanDoer() {
		return timespanDoer;
	}
	
	public SpecialDoer getSpecialDoer() {
		return specialDoer;
	}
	
	/**
	 * If it needs to, sets up the doers to have a static set to work with.
	 */
	private void setupDoers() {
		if(doersSetup) {
			return;
		}
		
		doersSetup = true;
		
		this.taskDoer = new TaskDoer(manager, this.outputter);
		this.workPeriodDoer = new WorkPeriodDoer(manager, this.outputter);
		this.timespanDoer = new TimespanDoer(workPeriodDoer, manager, this.outputter);
		this.specialDoer = new SpecialDoer(this, this.outputter);
	}
	
	/**
	 * Resets the static doers. Will clear data meant to be persistent between runs.
	 */
	public void resetDoers() {
		doersSetup = false;
		setupDoers();
	}
	
	/**
	 * Sets the latest period held by the manager as the selected period in the period doer. If
	 * there are no periods, sets the selected to null.
	 */
	public void setNewestPeriodAsSelectedQuiet() {
		setupDoers();
		if(manager.getWorkPeriods().isEmpty()) {
			workPeriodDoer.setSelected(null);
		} else {
			workPeriodDoer.setSelected(manager.getWorkPeriods().last());
		}
	}
	
	public ActionDoer getActionDoer(KeeperObjectType keeperObject) {
		switch(keeperObject) {
		case TASK:
			return this.getTaskDoer();
		case PERIOD:
			return this.getWorkPeriodDoer();
		case SPAN:
			return this.getTimespanDoer();
		default:
			throw new IllegalArgumentException("Bad keeper object given.");
		}
	}
	
	/**
	 * Gets the selected work period from the period doer.
	 *
	 * @return The selected work period from the period doer.
	 */
	public WorkPeriod getSelectedWorkPeriod() {
		return workPeriodDoer.getSelected();
	}
	
	/**
	 * Performs an action based on the config given.
	 *
	 * @param config The configuration for the run
	 * @return True if the manager was modified, false otherwise.
	 */
	public boolean doObjAction(ActionConfig config) {
		if(config == null) {
			return false;
		}
		setupDoers();
		if(config.getSpecialAction() != null) {
			return this.specialDoer.processSpecial(config);
		}
		
		if(config.getAction() == null) {
			log.warn("No action given.");
			outputter.normPrintln(DEFAULT, "No action given. Doing nothing.");
			return false;
		}
		if(config.getObjectOperatingOn() == null) {
			log.error("No object specified to operate on.");
			outputter.errorPrintln("No object specified to operate on");
			return false;
		}
		switch(config.getObjectOperatingOn()) {
		case TASK:
			return taskDoer.doAction(config);
		case PERIOD:
			return workPeriodDoer.doAction(config);
		case SPAN:
			return timespanDoer.doAction(config);
		}
		return false;
	}
}
