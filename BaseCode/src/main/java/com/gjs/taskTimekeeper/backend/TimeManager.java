package com.gjs.taskTimekeeper.backend;


import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Overall manager of WorkPeriods. Handles high level tasks.
 */
public class TimeManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeManager.class);

	/** Taks held by this object. */
	Set<Task> tasks = new HashSet<>();
	/** Work periods held by this object. */
	SortedSet<WorkPeriod> workPeriods = new TreeSet<>();

	/**
	 * Default constructor
	 */
	public TimeManager(){
	}

	/**
	 * Constructor to set tasks.
	 * @param tasks The tasks to set.
	 * @throws NullPointerException If the set of tasks if null.
	 */
	public TimeManager(@NotNull Set<Task> tasks) throws NullPointerException {
		this();
		this.setTasks(tasks);
	}

	/**
	 * Constructor to set tasks and work periods.
	 * @param tasks The tasks to set
	 * @param workPeriods The work periods to set.
	 * @throws NullPointerException If either tasks or work periods are set.
	 */
	public TimeManager(@NotNull Set<Task> tasks, @NotNull SortedSet<WorkPeriod> workPeriods) throws NullPointerException {
		this(tasks);
		this.setWorkPeriods(workPeriods);
	}

	/**
	 * Gets the work periods
	 * @return The work periods.
	 */
	@NotNull
	public SortedSet<WorkPeriod> getWorkPeriods() {
		return workPeriods;
	}

	/**
	 * Sets the work periods
	 * @param workPeriods The new work periods.
	 * @return This manager object.
	 * @throws NullPointerException If the periods given are null or the set contains a null value.
	 */
	@NotNull
	public TimeManager setWorkPeriods(@NotNull SortedSet<WorkPeriod> workPeriods) throws NullPointerException {
		if(workPeriods == null){
			throw new NullPointerException("Work periods cannot be null.");
		}
		if(workPeriods.contains(null)){
			throw new NullPointerException("Work periods cannot contain null period.");
		}
		this.workPeriods = workPeriods;
		return this;
	}

	/**
	 * Gets the tasks.
	 * @return The tasks held.
	 */
	@NotNull
	public Set<Task> getTasks() {
		return tasks;
	}

	/**
	 * Sets the tasks held by the manager.
	 * @param tasks The new tasks to be held.
	 * @return This manager object.
	 * @throws NullPointerException If the tasks list is null or contains null values.
	 */
	@NotNull
	public TimeManager setTasks(@NotNull Set<Task> tasks) throws NullPointerException {
		if(tasks == null){
			throw new NullPointerException("Tasks cannot be null.");
		}
		if(tasks.contains(null)){
			throw new NullPointerException("Tasks cannot hold a null value.");
		}
		this.tasks = tasks;
		return this;
	}

	/**
	 * Adds a task to the set.
	 * @param task The task to add
	 * @return This manager object
	 * @throws NullPointerException If the task given is null.
	 */
	@NotNull
	public TimeManager addTask(@NotNull Task task) throws NullPointerException{
		if(task == null){
			throw new NullPointerException("Cannot add a null task.");
		}
		this.getTasks().add(task);
		return this;
	}

	/**
	 * Cleans up the set of tasks by re-creating the set from held timespans.
	 * @return This manager object
	 */
	@NotNull
	public TimeManager cleanupTasks(){
		Set<Task> usedTasks = new HashSet<>();

		for(WorkPeriod period : this.getWorkPeriods()){
			for(Timespan span : period.getTimespans()){
				usedTasks.add(span.getTask());
			}
		}
		this.setTasks(usedTasks);

		return this;
	}

	/**
	 * Adds a work period
	 * @param workPeriod The work period to add
	 * @return This manager object
	 * @throws NullPointerException If the period given is null
	 */
	@NotNull
	public TimeManager addWorkPeriod(@NotNull WorkPeriod workPeriod) throws NullPointerException{
		if(workPeriod == null){
			throw new NullPointerException("Work period cannot be null.");
		}
		this.getWorkPeriods().add(workPeriod);
		return this;
	}

	/**
	 * Adds a timespan to the most recent work period.
	 * @param span The span to add
	 * @return This manager object
	 * @throws NullPointerException If the span given is null
	 * @throws IllegalStateException If there are no periods to add to.
	 */
	@NotNull
	public TimeManager addTimespan(@NotNull Timespan span) throws NullPointerException, IllegalStateException {
		if(span == null){
			throw new NullPointerException("Timespan cannot be null.");
		}
		if(this.getWorkPeriods().isEmpty()){
			throw new IllegalStateException("Do not have a work period to add to.");
		}
		this.addTask(span.getTask());
		this.workPeriods.last().addTimespan(span);
		return this;
	}

	/**
	 * Gets a list of periods with unfinished periods.
	 * @return a list of periods with unfinished periods.
	 */
	@NotNull
	public Collection<WorkPeriod> getUnfinishedPeriods(){
		List<WorkPeriod> periods = new LinkedList<>();

		for(WorkPeriod period : this.getWorkPeriods()){
			if(period.hasUnfinishedTimespans()){
				periods.add(period);
			}
		}

		return periods;
	}

	/**
	 * Determines if there are any unfinished periods.
	 * @return if there are any unfinished periods.
	 */
	public boolean hasUnfinishedPeriods(){
		for(WorkPeriod period : this.getWorkPeriods()){
			if(period.hasUnfinishedTimespans()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets all work periods with a given timespan.
	 * @param timespan The timespan to look for.
	 * @return all work periods with a given timespan.
	 */
	@NotNull
	public Collection<WorkPeriod> getWorkPeriodsWith(Timespan timespan){
		List<WorkPeriod> periods = new LinkedList<>();

		for(WorkPeriod period : this.getWorkPeriods()){
			if(period.contains(timespan)){
				periods.add(period);
			}
		}
		return periods;
	}

	/**
	 * Gets all work periods with the given task
	 * @param task The task to look for
	 * @return all work periods with the given task
	 */
	@NotNull
	public Collection<WorkPeriod> getWorkPeriodsWith(Task task){
		List<WorkPeriod> periods = new LinkedList<>();

		for(WorkPeriod period : this.getWorkPeriods()){
			if(period.hasTimespansWith(task)){
				periods.add(period);
			}
		}
		return periods;
	}

	/**
	 * Gets all timespans with the given task
	 * @param task The task to look for.
	 * @return all timespans with the given task
	 */
	@NotNull
	public Collection<Timespan> getTimespansWith(Task task){
		List<Timespan> periods = new LinkedList<>();

		for(WorkPeriod period : this.getWorkPeriods()){
			if(period.hasTimespansWith(task)){
				periods.addAll(period.getTimespansWith(task));
			}
		}
		return periods;
	}
}
