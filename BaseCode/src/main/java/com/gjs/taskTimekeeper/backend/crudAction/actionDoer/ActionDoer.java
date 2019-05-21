package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.*;
import com.gjs.taskTimekeeper.backend.crudAction.Action;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.gjs.taskTimekeeper.backend.crudAction.Action.ADD;

public abstract class ActionDoer <T extends KeeperObject> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDoer.class);

	/**
	 * Adds an object to the time manager.
	 * @param manager The manager being dealt with
	 * @param config The configuration to provide details for the addition.
	 * @return True if manager was modified, false otherwise.
	 */
	protected abstract boolean add(TimeManager manager, ActionConfig config);

	/**
	 * Edits an object in the time manager.
	 * @param manager The manager being dealt with
	 * @param config The configuration to provide details for the change.
	 * @return True if manager was modified, false otherwise.
	 */
	protected abstract boolean edit(TimeManager manager, ActionConfig config);

	/**
	 * Removes an object to the time manager.
	 * @param manager The manager being dealt with
	 * @param config The configuration to provide details for the removal.
	 * @return True if manager was modified, false otherwise.
	 */
	protected abstract boolean remove(TimeManager manager, ActionConfig config);

	/**
	 * Views objects in the time manager.
	 * @param manager The manager being dealt with
	 * @param config The configuration to provide details for the view (filtering, etc.)
	 * @return The objects included in the view
	 */
	public abstract void view(TimeManager manager, ActionConfig config);

	/**
	 * Displays a long output of the object given.
	 * @param object
	 */
	public abstract void displayOne(TimeManager manager, T object);

	/**
	 * Searches the time manager for the data object based on the config.
	 * @param manager
	 * @param config
	 * @return
	 */
	public abstract List<T> search(TimeManager manager, ActionConfig config);

	/**
	 * Gets an object based on its index in the search in the configuration given.
	 * @param manager
	 * @param config
	 * @return
	 */
	protected T getAtIndex(TimeManager manager, ActionConfig config){
		if(config.getIndex() == null){
			LOGGER.warn("No index given to get.");
			System.err.println("No index given to get.");
			return null;
		}
		List<T> results = this.search(manager, config);

		try{
			return results.get(config.getIndex() - 1);
		}catch (IndexOutOfBoundsException e){
			LOGGER.warn("Index given was out of bounds for the search.", e);
			System.err.println("Index given was out of bounds for the search.");
		}

		return null;
	}

	/**
	 * Gets the headers used when viewing objects. Starts with a "#" column, followed by the name of each of the data points to show.
	 * @return the headers used when viewing objects.
	 */
	public abstract List<String> getViewHeaders();

	/**
	 * Gets the values needed for viewing oone object in a {@link #view(TimeManager, ActionConfig)} call.
	 * @param object The object for the row
	 * @return List of values that make up the row.
	 */
	public abstract List<String> getViewRowEntries(int rowNum, T object);

	/**
	 * Prints out the collection of objects to stdin. Prints in an ASCII table format.
	 * @param objects The objects to print out.
	 */
	protected final void printView(List<T> objects){
		List<String> headers = this.getViewHeaders();
		List<List<String>> rows = this.getViewRows(objects);
		List<Integer> colWidths = getColWidths(headers, rows);

		String rowFormat = getRowFormat(colWidths);
		String hr = getViewHr(colWidths);

		System.out.println(hr);
		System.out.printf(rowFormat, headers.toArray());
		System.out.println(hr);
		for(List<String> row : rows){
			System.out.printf(rowFormat, row.toArray());
		}
		System.out.println(hr);

	}

	/**
	 * Gets the needed row data from the objects given to view. Calls {@link #getViewRowEntries(int, KeeperObject)} to do this.
	 * @param objects The objects to get the row data for.
	 * @return The needed row data from the objects given to view.
	 */
	private List<List<String>> getViewRows(List<T> objects){
		if(objects == null){
			return new LinkedList<>();
		}
		ArrayList<List<String>> output = new ArrayList<>(objects.size());

		int i = 0;
		for(T object : objects){
			output.add(this.getViewRowEntries(++i, object));
		}

		return output;
	}

	/**
	 * Gets the needed widths of columns for the table. Considers the lengths of the headers and all data entries in all the rows.
	 * @param headers The headers of the table
	 * @param rows The rows of the table
	 * @return the needed widths of columns for the table.
	 */
	private static List<Integer> getColWidths(List<String> headers, List<List<String>> rows) {
		ArrayList<Integer> output = new ArrayList<>(headers.size());

		for(int i = 0; i < headers.size(); i++){
			output.add(headers.get(i).length());
		}

		for(List<String> row : rows){
			for(int i = 0; i < row.size(); i++){
				if(output.get(i) < row.get(i).length()){
					output.set(i, row.get(i).length());
				}
			}
		}

		return output;
	}

	/**
	 * Gets the format for a row of the table for a {@link #view(TimeManager, ActionConfig)}.
	 * @param colWidths The widths of the columns
	 * @return the format for a row of the table.
	 */
	private static String getRowFormat(List<Integer> colWidths){
		StringBuilder builder = new StringBuilder();

		for(int colWidth : colWidths){
			builder.append("| %-").append(colWidth).append("s ");
		}
		builder.append("|%n");
		return builder.toString();
	}

	/**
	 * Gets the string that is a horizontal line in the table.
	 * @param colWidths The widths of the columns.
	 * @return the string that is a horizontal line in the table.
	 */
	private static String getViewHr(List<Integer> colWidths){
		StringBuilder builder = new StringBuilder();

		for(int width : colWidths){
			builder.append("+-");
			for(int i = 0; i <= width; i++){
				builder.append('-');
			}
		}
		builder.append('+');
		return builder.toString();
	}

	/**
	 * Performs an action on the manager. Will call one of: {@link #add(TimeManager, ActionConfig)}, {@link #edit(TimeManager, ActionConfig)}, {@link #remove(TimeManager, ActionConfig)}, {@link #view(TimeManager, ActionConfig)}
	 * @param manager The manager being dealt with
	 * @param config The configuration for the run
	 * @return True if the manager was modified, false otherwise.
	 */
	public final boolean doAction(TimeManager manager, ActionConfig config){
		boolean changed = false;
		switch (config.getAction()){
			case ADD:
				changed = this.add(manager, config);
				break;
			case EDIT:
				changed = this.edit(manager, config);
				break;
			case REMOVE:
				changed = this.remove(manager, config);
				break;
			case VIEW:
				this.view(manager, config);
				break;
			default:
				LOGGER.warn("No action given.");
				System.err.println("No action given.");
		}
		return changed;
	}

	private static TaskDoer TASK_DOER;
	private static PeriodDoer PERIOD_DOER;
	private static TimespanDoer TIMESPAN_DOER;
	private static boolean doersSetup = false;

	private static void setupDoers(){
		if(doersSetup){
			return;
		}

		doersSetup = true;
		TASK_DOER = new TaskDoer();
		PERIOD_DOER = new PeriodDoer();
		TIMESPAN_DOER = new TimespanDoer(PERIOD_DOER);
	}

	public static void resetDoers(){
		doersSetup = false;
		setupDoers();
	}

	public static WorkPeriod getSelectedWorkPeriod(){
		return PERIOD_DOER.getSelected();
	}

	/**
	 * Performs an action based on the config given. Creates a doer of the appropriate type and calls its {@link #doAction(TimeManager, ActionConfig)}
	 * @param manager The manager being dealt with
	 * @param config The configuration for the run
	 * @return True if the manager was modified, false otherwise.
	 */
	public static boolean doObjAction(TimeManager manager, ActionConfig config){
		setupDoers();
		if(config.getSpecialAction() != null){
			return processSpecial(manager, config);
		}
		if(config.getObjectOperatingOn() == null){
			LOGGER.error("No object specified to operate on.");
			System.err.println("No object specified to operate on");
			return false;
		}
		switch (config.getObjectOperatingOn()){
			case TASK:
				return TASK_DOER.doAction(manager, config);
			case PERIOD:
				return PERIOD_DOER.doAction(manager, config);
			case SPAN:
				return TIMESPAN_DOER.doAction(manager, config);
		}
		return false;
	}

	/**
	 * TODO:: document, test
	 * @param manager
	 * @param config
	 * @return
	 */
	public static boolean processSpecial(TimeManager manager, ActionConfig config){
		switch (config.getSpecialAction()){
			case "newSpan":
				return setupForAddSpanNow(manager, config);
			case "selectNewest": {
				ActionConfig actionConfig = new ActionConfig()
					.setObjectOperatingOn(KeeperObjectType.PERIOD)
					.setAction(Action.VIEW)
					.setIndex(manager.getWorkPeriods().size())
					.setSelect(true);
				return doObjAction(manager, actionConfig);
			}
			case "newPeriod": {
				ActionConfig actionConfig = new ActionConfig()
					.setObjectOperatingOn(KeeperObjectType.PERIOD)
					.setAction(ADD)
					.setSelect(true);
				return doObjAction(manager, actionConfig);
			}
			default:
				LOGGER.error("No valid special command given.");
				System.err.println("No valid special command given.");
				return false;
		}
	}

	public static boolean setupForAddSpanNow(TimeManager manager, ActionConfig config){
		WorkPeriod selected = getSelectedWorkPeriod();
		Task task = manager.getTaskByName(config.getName());
		LOGGER.info("Setting up config for adding a span now.");
		if(selected == null){
			LOGGER.error("No work period selected.");
			System.err.println("No work period selected.");
			return false;
		}
		if(task == null){
			LOGGER.error("No task with name specified.");
			System.err.println("No task with name specified.");
			return false;
		}
		//finish unfinished spans
		if(selected.isUnfinished()){
			LOGGER.debug("Attempting to finish spans in selected periods.");
			System.out.println("Attempting to finish spans in selected periods.");
			for(Timespan span : selected.getUnfinishedTimespans()){
				if(!span.hasStartTime()){
					continue;
				}
				if(!span.hasEndTime()){
					span.setEndTime(LocalDateTime.now());
				}
			}
		}

		selected.addTimespan(
			new Timespan(task, LocalDateTime.now())
		);
		return true;
	}
}
