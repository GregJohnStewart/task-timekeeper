package com.gjs.taskTimekeeper.backend.crudAction.actionDoer;

import com.gjs.taskTimekeeper.backend.KeeperObject;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ActionDoer <T extends KeeperObject> {

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
	 * Searches the time manager for the data object based on the config.
	 * @param manager
	 * @param config
	 * @return
	 */
	public abstract Collection<T> search(TimeManager manager, ActionConfig config);

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
	protected final void printView(Collection<T> objects){
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
	private List<List<String>> getViewRows(Collection<T> objects){
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
				//TODO:: output nothing specified?
		}
		return changed;
	}

	/**
	 * Performs an action based on the config given. Creates a doer of the appropriate type and calls its {@link #doAction(TimeManager, ActionConfig)}
	 * @param manager The manager being dealt with
	 * @param config The configuration for the run
	 * @return True if the manager was modified, false otherwise.
	 */
	public static boolean doObjAction(TimeManager manager, ActionConfig config){
		switch (config.getObjectOperatingOn()){
			case TASK:
				return new TaskDoer().doAction(manager, config);
			case PERIOD:
				//TODO:: make doer
				break;
			case SPAN:
				//TODO:: make doer
				break;
		}
		return false;
	}
}
