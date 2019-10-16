package com.gjs.taskTimekeeper.baseCode.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.KeeperObject;
import com.gjs.taskTimekeeper.baseCode.TimeManager;
import com.gjs.taskTimekeeper.baseCode.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.utils.Outputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.gjs.taskTimekeeper.baseCode.utils.OutputLevel.DEFAULT;

/**
 * Main class to be used to perform actions. Provides a 'main' method ({@link #doAction(TimeManager, ActionConfig)} to perform said action.
 * TODO:: make as an instantiable class to make it easy to be multithreaded
 * @param <T> For extending object action doers, the object the action performs operations on.
 */
public abstract class ActionDoer <T extends KeeperObject> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDoer.class);

	protected static Outputter OUTPUTTER = new Outputter();

	public static void setOutputter(Outputter outputter){
		OUTPUTTER = outputter;
	}
	public static Outputter getOutputter(){
		return OUTPUTTER;
	}

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
	 */
	public abstract void view(TimeManager manager, ActionConfig config);

	/**
	 * Displays a long output of the object given.
	 * @param manager The manager being dealt with.
	 * @param object The object being viewed.
	 */
	public abstract void displayOne(TimeManager manager, T object);

	/**
	 * Searches the time manager for the data object based on the config.
	 * @param manager The manager being dealt with
	 * @param config The configuration to provide details for the search.
	 * @return The list of matched objects.
	 */
	public abstract List<T> search(TimeManager manager, ActionConfig config);

	/**
	 * Gets an object based on its index in the search in the configuration given.
	 * @param manager The manager being dealt with
	 * @param config The configuration to provide details for the search and index to get.
	 * @return The object at the index of the search given. Null if index out of bounds or no index given.
	 */
	protected T getAtIndex(TimeManager manager, ActionConfig config){
		if(config.getIndex() == null){
			//LOGGER.warn("No index given to get.");
			//System.err.println("No index given to get.");
			return null;
		}
		List<T> results = this.search(manager, config);

		try{
			return results.get(config.getIndex() - 1);
		}catch (IndexOutOfBoundsException e){
			LOGGER.warn("Index given was out of bounds for the search.", e);
			OUTPUTTER.errorPrintln("Index given was out of bounds for the search.");
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
	 * Prints out the collection of objects to stdout. Prints in an ASCII table format.
	 * @param objects The objects to print out.
	 */
	protected final void printView(String title, List<T> objects){
		for(String textRow : this.getView(title, objects)){
			System.out.println(textRow);
		}
	}

	protected final List<String> getView(String title, List<T> objects){
		List<String> headers = this.getViewHeaders();
		List<List<String>> rows = this.getViewRows(objects);
		List<Integer> colWidths = getColWidths(headers, rows);

		String rowFormat = getRowFormat(colWidths);
		String hr = getViewHr(colWidths);

		List<String> output = new ArrayList<>();

		if(title != null) {
			output.add(title + ":");
		}
		output.add(hr);
		output.add(String.format(rowFormat, headers.toArray()));
		output.add(hr);
		for(List<String> row : rows){
			output.add(String.format(rowFormat, row.toArray()));
		}
		output.add(hr);

		return output;
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

		for(String header : headers){
			output.add(header.length());
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
		builder.append("|");
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
			builder.append("-".repeat(width + 1));
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
				OUTPUTTER.errorPrintln("No action given.");
		}
		return changed;
	}

	/** An instance of a task doer. */
	private static TaskDoer TASK_DOER;
	/** An instance of a period doer. */
	private static WorkPeriodDoer PERIOD_DOER;
	/** An instance of a timespan doer. */
	private static TimespanDoer TIMESPAN_DOER;
	/** If the doers have already been setup. */
	private static boolean doersSetup = false;

	/**
	 * If it needs to, sets up the doers to have a static set to work with.
	 */
	private static void setupDoers(){
		if(doersSetup){
			return;
		}

		doersSetup = true;
		TASK_DOER = new TaskDoer();
		PERIOD_DOER = new WorkPeriodDoer();
		TIMESPAN_DOER = new TimespanDoer(PERIOD_DOER);
	}

	/**
	 * Resets the static doers. Will clear data meant to be persistent between runs.
	 */
	public static void resetDoers(){
		doersSetup = false;
		setupDoers();
	}

	/**
	 * Sets the latest period held by the manager as the selected period in the period doer.
	 * If there are no periods, sets the selected to null.
	 * @param manager The manager being dealt with.
	 */
	public static void setNewestPeriodAsSelectedQuiet(TimeManager manager){
		setupDoers();
		if(manager.getWorkPeriods().isEmpty()){
			PERIOD_DOER.setSelected(null);
		} else {
			PERIOD_DOER.setSelected(
				manager.getWorkPeriods()
					.last()
			);
		}
	}

	public static ActionDoer getActionDoer(KeeperObjectType keeperObject){
		switch (keeperObject){
			case TASK:
				return TASK_DOER;
			case PERIOD:
				return PERIOD_DOER;
			case SPAN:
				return TIMESPAN_DOER;
			default:
				throw new IllegalArgumentException("Bad keeper object given.");
		}
	}

	/**
	 * Gets the selected work period from the period doer.
	 * @return The selected work period from the period doer.
	 */
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
			return SpecialDoer.processSpecial(manager, config);
		}

		if(config.getAction() == null){
			LOGGER.warn("No action given.");
			OUTPUTTER.normPrintln(DEFAULT, "No action given. Doing nothing.");
			return false;
		}
		if(config.getObjectOperatingOn() == null){
			LOGGER.error("No object specified to operate on.");
			OUTPUTTER.errorPrintln("No object specified to operate on");
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

	protected static Map<String, String> parseAttributes(String attString) throws IllegalArgumentException{
		if(attString == null){
			throw new IllegalArgumentException("Attribute string was null.");
		}
		Map<String, String> output = new HashMap<>();
		if(!attString.equals("EMPTY")) {
			String[] attPairs = attString.split(";");
			for (String attPairString : attPairs) {
				String[] attPair = attPairString.split(",");
				if (attPair.length != 2) {
					throw new IllegalArgumentException("Bad attribute pairs given. Bad pair: " + attPairString);
				}
				output.put(attPair[0], attPair[1]);
			}
		}
		return output;
	}
}
