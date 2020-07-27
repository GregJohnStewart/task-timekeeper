package com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.objects.KeeperObject;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public abstract class CrudDoer <T extends KeeperObject> extends ActionDoer {
	
	public CrudDoer(TimeManager manager) {
		super(manager);
	}
	
	public CrudDoer(TimeManager manager, Outputter outputter) {
		super(manager, outputter);
	}
	
	// <editor-fold desc="CRUD methods">
	
	/**
	 * Adds an object to the time manager.
	 *
	 * @param config The configuration to provide details for the addition.
	 * @return True if manager was modified, false otherwise.
	 */
	protected abstract boolean add(ActionConfig config);
	
	/**
	 * Edits an object in the time manager.
	 *
	 * @param config The configuration to provide details for the change.
	 * @return True if manager was modified, false otherwise.
	 */
	protected abstract boolean edit(ActionConfig config);
	
	/**
	 * Removes an object to the time manager.
	 *
	 * @param config The configuration to provide details for the removal.
	 * @return True if manager was modified, false otherwise.
	 */
	protected abstract boolean remove(ActionConfig config);
	
	/**
	 * Views objects in the time manager.
	 *
	 * @param config The configuration to provide details for the view (filtering, etc.)
	 */
	public abstract void view(ActionConfig config);
	
	/**
	 * Displays a long output of the object given.
	 *
	 * @param object The object being viewed.
	 */
	public abstract void displayOne(T object);
	
	/**
	 * Searches the time manager for the data object based on the config.
	 *
	 * @param config The configuration to provide details for the search.
	 * @return The list of matched objects.
	 */
	public abstract List<T> search(ActionConfig config);
	
	public List<T> search() {
		return this.search(new ActionConfig());
	}
	
	/**
	 * Gets an object based on its index in the search in the configuration given.
	 *
	 * @param config The configuration to provide details for the search and index to get.
	 * @return The object at the index of the search given. Null if index out of bounds or no index
	 * 	given.
	 */
	protected T getAtIndex(ActionConfig config) {
		if(config.getIndex() == null) {
			// LOGGER.warn("No index given to get.");
			// System.err.println("No index given to get.");
			return null;
		}
		List<T> results = this.search(config);
		
		try {
			return results.get(config.getIndex() - 1);
		} catch(IndexOutOfBoundsException e) {
			log.warn("Index given was out of bounds for the search.", e);
			outputter.errorPrintln("Index given was out of bounds for the search.");
		}
		
		return null;
	}
	
	/**
	 * Performs an action on the manager.
	 *
	 * @param config The configuration for the run
	 * @return True if the manager was modified, false otherwise.
	 */
	public final boolean doAction(ActionConfig config) {
		boolean changed = false;
		switch(config.getAction()) {
		case ADD:
			changed = this.add(config);
			break;
		case EDIT:
			changed = this.edit(config);
			break;
		case REMOVE:
			changed = this.remove(config);
			break;
		case VIEW:
			this.view(config);
			break;
		default:
			log.warn("No action given.");
			outputter.errorPrintln("No action given.");
		}
		return changed;
	}
	// </editor-fold>
	
	// <editor-fold desc="Output Viewing methods">
	
	/**
	 * Gets the headers used when viewing objects. Starts with a "#" column, followed by the name of
	 * each of the data points to show.
	 *
	 * @return the headers used when viewing objects.
	 */
	public abstract List<String> getViewHeaders();
	
	/**
	 * Gets the values needed for viewing oone object in a {@link #view(ActionConfig)} call.
	 *
	 * @param object The object for the row
	 * @return List of values that make up the row.
	 */
	public abstract List<String> getViewRowEntries(int rowNum, T object);
	
	/**
	 * Prints out the collection of objects to stdout. Prints in an ASCII table format.
	 *
	 * @param objects The objects to print out.
	 */
	protected final void printView(String title, List<T> objects) {
		for(String textRow : this.getView(title, objects)) {
			System.out.println(textRow);
		}
	}
	
	protected final List<String> getView(String title, List<T> objects) {
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
		for(List<String> row : rows) {
			output.add(String.format(rowFormat, row.toArray()));
		}
		output.add(hr);
		
		return output;
	}
	
	/**
	 * Gets the needed row data from the objects given to view. Calls {@link #getViewRowEntries(int,
	 * KeeperObject)} to do this.
	 *
	 * @param objects The objects to get the row data for.
	 * @return The needed row data from the objects given to view.
	 */
	private List<List<String>> getViewRows(List<T> objects) {
		if(objects == null) {
			return new LinkedList<>();
		}
		ArrayList<List<String>> output = new ArrayList<>(objects.size());
		
		int i = 0;
		for(T object : objects) {
			output.add(this.getViewRowEntries(++i, object));
		}
		
		return output;
	}
	
	/**
	 * Gets the needed widths of columns for the table. Considers the lengths of the headers and all
	 * data entries in all the rows.
	 *
	 * @param headers The headers of the table
	 * @param rows    The rows of the table
	 * @return the needed widths of columns for the table.
	 */
	private static List<Integer> getColWidths(List<String> headers, List<List<String>> rows) {
		ArrayList<Integer> output = new ArrayList<>(headers.size());
		
		for(String header : headers) {
			output.add(header.length());
		}
		
		for(List<String> row : rows) {
			for(int i = 0; i < row.size(); i++) {
				if(output.get(i) < row.get(i).length()) {
					output.set(i, row.get(i).length());
				}
			}
		}
		
		return output;
	}
	
	/**
	 * Gets the format for a row of the table for a {@link #view(ActionConfig)}.
	 *
	 * @param colWidths The widths of the columns
	 * @return the format for a row of the table.
	 */
	private static String getRowFormat(List<Integer> colWidths) {
		StringBuilder builder = new StringBuilder();
		
		for(int colWidth : colWidths) {
			builder.append("| %-").append(colWidth).append("s ");
		}
		builder.append("|");
		return builder.toString();
	}
	
	/**
	 * Gets the string that is a horizontal line in the table.
	 *
	 * @param colWidths The widths of the columns.
	 * @return the string that is a horizontal line in the table.
	 */
	private static String getViewHr(List<Integer> colWidths) {
		StringBuilder builder = new StringBuilder();
		
		for(int width : colWidths) {
			builder.append("+-");
			for(int i = 0; i <= (width); i++) {
				builder.append("-");
			}
		}
		builder.append('+');
		return builder.toString();
	}
	// </editor-fold>
}
