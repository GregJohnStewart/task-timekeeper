package com.gjs.taskTimekeeper.baseCode.core.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer.CrudOperator;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Overall manager of WorkPeriods. Handles high level tasks.
 * <p>
 * TODO:: investigate using lombok
 */
public class TimeManager {
	/**
	 * Tasks held by this object.
	 */
	private SortedSet<Task> tasks = new TreeSet<>();
	/**
	 * Work periods held by this object.
	 */
	private SortedSet<WorkPeriod> workPeriods = new TreeSet<>();
	/**
	 * The object to handle CRUD operations
	 */
	@JsonIgnore
	private final CrudOperator crudOperator;
	
	/**
	 * Default constructor
	 */
	public TimeManager() {
		this.crudOperator = new CrudOperator(this);
	}
	
	/**
	 * Constructor to set tasks.
	 *
	 * @param tasks The tasks to set.
	 * @throws NullPointerException If the set of tasks if null.
	 */
	public TimeManager(SortedSet<Task> tasks) throws NullPointerException {
		this();
		this.setTasks(tasks);
	}
	
	/**
	 * Constructor to set tasks and work periods.
	 *
	 * @param tasks       The tasks to set
	 * @param workPeriods The work periods to set.
	 * @throws NullPointerException If either tasks or work periods are set.
	 */
	public TimeManager(SortedSet<Task> tasks, SortedSet<WorkPeriod> workPeriods)
		throws NullPointerException {
		this(tasks);
		this.setWorkPeriods(workPeriods);
	}
	
	public CrudOperator getCrudOperator() {
		return this.crudOperator;
	}
	
	public boolean doCrudAction(ActionConfig config) {
		return this.getCrudOperator().doObjAction(config);
	}
	
	/**
	 * Gets the work periods
	 *
	 * @return The work periods.
	 */
	public SortedSet<WorkPeriod> getWorkPeriods() {
		return workPeriods;
	}
	
	/**
	 * Sets the work periods
	 *
	 * @param workPeriods The new work periods.
	 * @return This manager object.
	 * @throws NullPointerException If the periods given are null or the set contains a null value.
	 */
	public TimeManager setWorkPeriods(SortedSet<WorkPeriod> workPeriods, boolean cleanupTasks)
		throws NullPointerException {
		if(workPeriods == null) {
			throw new NullPointerException("Work periods cannot be null.");
		}
		
		if(!workPeriods.isEmpty()) {
			boolean containsNullValues = false;
			
			try {
				containsNullValues = workPeriods.contains(null);
			} catch(NullPointerException e) {
				// nothing to do
			}
			if(containsNullValues) {
				throw new NullPointerException("Cannot hold null timespans.");
			}
		}
		this.workPeriods = workPeriods;
		for(WorkPeriod period : this.getWorkPeriods()) {
			for(Name task : period.getTaskNames()) {
				if(this.getTaskByName(task) == null) {
					this.addTask(new Task(task));
				}
			}
		}
		if(cleanupTasks) {
			this.cleanupTasks();
		}
		
		return this;
	}
	
	/**
	 * Sets the work periods. Wrapper for {@link #setWorkPeriods(SortedSet, boolean)}, not updating
	 * tasks.
	 *
	 * @param workPeriods The work periods to set.
	 * @return This object.
	 * @throws NullPointerException If the periods given are null or the set contains a null value.
	 */
	public TimeManager setWorkPeriods(SortedSet<WorkPeriod> workPeriods)
		throws NullPointerException {
		this.setWorkPeriods(workPeriods, false);
		return this;
	}
	
	/**
	 * Gets the tasks.
	 *
	 * @return The tasks held.
	 */
	public SortedSet<Task> getTasks() {
		return tasks;
	}
	
	/**
	 * Sets the tasks held by the manager.
	 *
	 * @param tasks The new tasks to be held.
	 * @return This manager object.
	 * @throws NullPointerException     If the tasks list is null or contains null values.
	 * @throws IllegalArgumentException If the tasks list has tasks with duplicate names
	 */
	public TimeManager setTasks(SortedSet<Task> tasks)
		throws NullPointerException, IllegalArgumentException {
		if(tasks == null) {
			throw new NullPointerException("Tasks cannot be null.");
		}
		
		if(!tasks.isEmpty()) {
			boolean containsNullValues = false;
			try {
				containsNullValues = tasks.contains(null);
			} catch(NullPointerException e) {
				// nothing to do
			}
			if(containsNullValues) {
				throw new NullPointerException("Cannot hold null tasks.");
			}
		}
		
		this.tasks = tasks;
		return this;
	}
	
	/**
	 * Adds a task to the set.
	 *
	 * @param task The task to add
	 * @return This manager object
	 * @throws NullPointerException     If the task given is null.
	 * @throws IllegalArgumentException If the task given's name equals the name of a task already
	 *                                  held.
	 */
	public TimeManager addTask(Task task) throws NullPointerException, IllegalArgumentException {
		if(task == null) {
			throw new NullPointerException("Cannot add a null task.");
		}
		for(Task heldTask : this.getTasks()) {
			if(heldTask.getName().equals(task.getName())) {
				throw new IllegalArgumentException("Task with same name already present.");
			}
		}
		this.getTasks().add(task);
		return this;
	}
	
	/**
	 * Cleans up the set of tasks by re-creating the set from held timespans.
	 *
	 * @return This manager object
	 */
	public TimeManager cleanupTasks() {
		SortedSet<Task> toKeep = new TreeSet<>();
		
		for(WorkPeriod period : this.getWorkPeriods()) {
			for(Name taskName : period.getTaskNames()) {
				Task task = this.getTaskByName(taskName);
				if(task != null) {
					toKeep.add(task);
				}
			}
		}
		
		this.setTasks(toKeep);
		return this;
	}
	
	/**
	 * Adds a work period. If any spans in the period aren't in the existing task list, they are
	 * added.
	 *
	 * @param workPeriod The work period to add
	 * @return This manager object
	 * @throws NullPointerException If the period given is null
	 */
	public TimeManager addWorkPeriod(WorkPeriod workPeriod) throws NullPointerException {
		if(workPeriod == null) {
			throw new NullPointerException("Work period cannot be null.");
		}
		this.getWorkPeriods().add(workPeriod);
		for(Name curTask : workPeriod.getTaskNames()) {
			if(this.getTaskByName(curTask) == null) {
				this.addTask(new Task(curTask));
			}
		}
		return this;
	}
	
	/**
	 * Adds a timespan to the most recent work period.
	 *
	 * @param span The span to add
	 * @return This manager object
	 * @throws NullPointerException     If the span given is null
	 * @throws IllegalArgumentException If the task in the span is not held by the manager and it
	 *                                  holds a duplicate name.
	 */
	public TimeManager addTimespan(Timespan span)
		throws NullPointerException, IllegalArgumentException {
		if(span == null) {
			throw new NullPointerException("Timespan cannot be null.");
		}
		if(this.getWorkPeriods().isEmpty()) {
			this.addWorkPeriod(new WorkPeriod());
		}
		if(this.getTaskByName(span.getTaskName()) == null) {
			this.addTask(new Task(span.getTaskName()));
		}
		this.workPeriods.last().addTimespan(span);
		return this;
	}
	
	/**
	 * Gets a list of periods with unfinished periods.
	 *
	 * @return a list of periods with unfinished periods.
	 */
	@JsonIgnore
	public Collection<WorkPeriod> getUnfinishedPeriods() {
		List<WorkPeriod> periods = new LinkedList<>();
		
		for(WorkPeriod period : this.getWorkPeriods()) {
			if(period.isUnCompleted()) {
				periods.add(period);
			}
		}
		
		return periods;
	}
	
	/**
	 * Determines if there are any unfinished periods.
	 *
	 * @return if there are any unfinished periods.
	 */
	public boolean hasUnfinishedPeriods() {
		for(WorkPeriod period : this.getWorkPeriods()) {
			if(period.isUnCompleted()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets all work periods with the given task
	 *
	 * @param task The task to look for
	 * @return all work periods with the given task
	 */
	public Collection<WorkPeriod> getWorkPeriodsWith(Task task) {
		List<WorkPeriod> periods = new LinkedList<>();
		
		for(WorkPeriod period : this.getWorkPeriods()) {
			if(period.hasTimespansWith(task)) {
				periods.add(period);
			}
		}
		return periods;
	}
	
	/**
	 * Gets all timespans with the given task
	 *
	 * @param task The task to look for.
	 * @return all timespans with the given task
	 */
	public Collection<Timespan> getTimespansWith(Task task) {
		List<Timespan> periods = new LinkedList<>();
		
		for(WorkPeriod period : this.getWorkPeriods()) {
			if(period.hasTimespansWith(task)) {
				periods.addAll(period.getTimespansWith(task));
			}
		}
		return periods;
	}
	
	/**
	 * Gets tasks by their exact name.
	 *
	 * @param name The name of the task to get.
	 * @return The task if the name matched. Null if no task has the exact name.
	 */
	public Task getTaskByName(String name) throws IllegalArgumentException, NullPointerException {
		return this.getTaskByName(new Name(name));
	}
	
	public Task getTaskByName(Name name) throws NullPointerException {
		if(name == null) {
			throw new NullPointerException("Name can't be null.");
		}
		for(Task task : this.tasks) {
			if(task.getName().equals(name)) {
				return task;
			}
		}
		return null;
	}
	
	/**
	 * Gets tasks based off of a regex pattern.
	 *
	 * @param pattern The pattern to match.
	 * @return The list of tasks that have a matching name.
	 */
	public List<Task> getTasksByNamePattern(Pattern pattern) {
		List<Task> tasks = new LinkedList<>();
		for(Task task : this.tasks) {
			if(pattern.matcher(task.getName().getName()).matches()) {
				tasks.add(task);
			}
		}
		return tasks;
	}
	
	/**
	 * Gets tasks based off of a regex pattern.
	 *
	 * @param pattern The pattern to match.
	 * @return The list of tasks that have a matching name.
	 */
	public List<Task> getTasksByNamePattern(String pattern) {
		return this.getTasksByNamePattern(Pattern.compile(pattern));
	}
	
	public void updateTaskName(Name oldName, Name newName) {
		if(oldName == null || newName == null) {
			throw new NullPointerException("Either new or old name given to update with was null.");
		}
		if(oldName.equals(newName)) {
			throw new IllegalArgumentException("Old task name was the same as the new name.");
		}
		if(this.getTaskByName(newName) != null) {
			throw new IllegalArgumentException("New task name was already present.");
		}
		Task taskWithOldName = this.getTaskByName(oldName);
		if(this.getTaskByName(oldName) == null) {
			throw new IllegalArgumentException("Old task name wasn't the name of any task.");
		}
		
		for(Timespan span : this.getTimespansWith(taskWithOldName)) {
			span.setTaskName(newName);
		}
		taskWithOldName.setName(newName);
	}
	
	public void updateTaskName(Task task, Name newName) {
		this.updateTaskName(task.getName(), newName);
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof TimeManager)) {
			return false;
		}
		TimeManager manager = (TimeManager)o;
		boolean base =
			getTasks().equals(manager.getTasks())
				&& getWorkPeriods().equals(manager.getWorkPeriods());
		
		// re-check work periods held. Needed because th TreeSet uses compare() to determine
		// equality, which doesn't consider attributes
		if(base) {
			Iterator<WorkPeriod> thisIt = this.getWorkPeriods().iterator();
			Iterator<WorkPeriod> oIt = manager.getWorkPeriods().iterator();
			
			while(thisIt.hasNext() && oIt.hasNext()) {
				WorkPeriod thisCur = thisIt.next();
				WorkPeriod oCur = oIt.next();
				if(!thisCur.equals(oCur)) {
					return false;
				}
			}
			
			base = !thisIt.hasNext() && !oIt.hasNext();
		} else {
			return false;
		}
		
		// re-check work tasks held. Needed because th TreeSet uses compare() to determine equality,
		// which doesn't consider attributes
		if(base) {
			Iterator<Task> thisIt = this.getTasks().iterator();
			Iterator<Task> oIt = manager.getTasks().iterator();
			while(thisIt.hasNext() && oIt.hasNext()) {
				Task thisCur = thisIt.next();
				Task oCur = oIt.next();
				if(!thisCur.equals(oCur)) {
					return false;
				}
			}
			return !thisIt.hasNext() && !oIt.hasNext();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getTasks(), getWorkPeriods());
	}
	
	@Override
	public TimeManager clone() {
		TimeManager newManager = new TimeManager();
		
		for(Task task : this.tasks) {
			newManager.addTask(task.clone());
		}
		
		TreeSet<WorkPeriod> newPeriods = new TreeSet<>();
		for(WorkPeriod period : this.workPeriods) {
			newPeriods.add(period.clone());
		}
		
		for(WorkPeriod period : newPeriods) {
			for(Timespan span : period.getTimespans()) {
				span.setTaskName(newManager.getTaskByName(span.getTaskName()));
			}
			newManager.addWorkPeriod(period);
		}
		
		return newManager;
	}
}
