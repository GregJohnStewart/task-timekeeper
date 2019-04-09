package com.gjs.taskTimekeeper.backend;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Defines a span of time. Associated with a {@link Task}.
 */
public class Timespan implements Comparable<Timespan>{

	/** The task this timespan is associated with. */
	private Task task;
	/** The start time of this span. */
	private LocalDateTime startTime;
	/** The ending time of this span. */
	private LocalDateTime endTime;
	/** The duration of the timespan. Null if {@link #startTime} or {@link #endTime} are null. */
	private Duration duration;

	/**
	 * Constructor to set the task
	 * @param task The task to set
	 * @throws NullPointerException If the task given is null
	 */
	public Timespan(Task task) throws NullPointerException {
		this.setTask(task);
	}

	/**
	 * Constructor to set the task
	 * @param task The task to set
	 * @param startTime The time this span started at.
	 * @throws NullPointerException If the task given is null
	 */
	public Timespan(Task task, LocalDateTime startTime) throws NullPointerException {
		this(task);
		this.setStartTime(startTime);
	}

	/**
	 * Constructor to set the task
	 * @param task The task to set
	 * @param startTime The time this span started at.
	 * @param endTime The time this span ended at.
	 * @throws NullPointerException If the task given is null
	 */
	public Timespan(Task task, LocalDateTime startTime, LocalDateTime endTime) throws NullPointerException {
		this(task, startTime);
		this.setEndTime(endTime);
	}

	/**
	 * Gets the {@link #task}.
	 * @return The {@link #task} this span is associated with
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * Sets the task this span is associated with.
	 * @param task The task to set
	 * @return This span object
	 * @throws NullPointerException If the task given is null
	 */
	public Timespan setTask(Task task) throws NullPointerException {
		if(task == null){
			throw new NullPointerException("Task cannot be null.");
		}
		this.task = task;
		return this;
	}

	/**
	 * Gets the start datetime.
	 * @return The start datetime. Null if not set.
	 */
	public LocalDateTime getStartTime() {
		return startTime;
	}

	/**
	 * Sets the start time for this span.
	 * @param startTime The new starting datetime.
	 * @return This span object
	 * @throws IllegalArgumentException If the start time given is after the end time.
	 */
	public Timespan setStartTime(LocalDateTime startTime) throws IllegalArgumentException {
		if(startTime != null && this.hasEndTime() && this.endTime.isBefore(startTime)){
			throw new IllegalArgumentException("Start time cannot be before end time.");
		}
		this.startTime = startTime;
		this.resetDuration();
		return this;
	}

	/**
	 * Gets the end time for this span.
	 * @return The end time for this span
	 */
	public LocalDateTime getEndTime() {
		return endTime;
	}

	/**
	 * Sets the end time for this object.
	 * @param endTime The new end datetime
	 * @return This span object
	 * @throws IllegalArgumentException If the end time given is before the start time.
	 */
	public Timespan setEndTime(LocalDateTime endTime) throws IllegalArgumentException {
		if(endTime != null && this.startTime != null && this.startTime.isAfter(endTime)){
			throw new IllegalArgumentException("Start time cannot be before end time.");
		}
		this.endTime = endTime;
		this.resetDuration();
		return this;
	}

	/**
	 * Determines if this span has a start time set.
	 * @return if this span has a start time set.
	 */
	public boolean hasStartTime(){
		return this.startTime != null;
	}

	/**
	 * Determines if this span has a end time set.
	 * @return if this span has a end time set.
	 */
	public boolean hasEndTime(){
		return this.endTime != null;
	}

	/**
	 * Determines if both start and end times are set.
	 * @return if both start and end times are set.
	 */
	public boolean isComplete(){
		return this.hasStartTime() && this.hasEndTime();
	}

	/**
	 * Gets the duration between the start and end times. Only available if both set.
	 * @return the duration between the start and end times.
	 * @throws IllegalStateException If either start or end times are not set.
	 */
	public Duration getDuration() throws IllegalStateException {
		if(duration == null || !this.isComplete()){
			throw new IllegalStateException("Start and end times not set. Cannot determine duration.");
		}
		return duration;
	}

	/**
	 * Resets the {@link #duration} variable. Null if not {@link #isComplete()}
	 */
	private void resetDuration(){
		if(!this.isComplete()){
			this.duration = null;
			return;
		}
		this.duration = Duration.between(this.getStartTime(), this.getEndTime());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Timespan)) return false;
		Timespan timespan = (Timespan) o;
		return getTask().equals(timespan.getTask()) &&
			Objects.equals(getStartTime(), timespan.getStartTime()) &&
			Objects.equals(getEndTime(), timespan.getEndTime());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTask(), getStartTime(), getEndTime());
	}

	/**
	 * Compares this to another timespan; Compares start time.
	 *
	 * Returns:
	 *   <0 if this begins before o
	 *   -1 if o has no start
	 *   >0 if this begins after o
	 *    1 if this has no start
	 *    this.getTask().compare(o.getTask()) if no start times or times are equal
	 *
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(Timespan o) throws NullPointerException  {
		if(o == null){
			throw new NullPointerException("Cannot compare to null.");
		}
		//if one or both don't have start time
		if(!this.hasStartTime() && !o.hasStartTime()){
			return this.getTask().compareTo(o.getTask());
		}
		if(!o.hasStartTime()){
			return -1;
		}
		if(!this.hasStartTime()){
			return 1;
		}

		int compareResult = this.getStartTime().compareTo(o.getStartTime());
		//if both have same start time, compare end times.
		if(compareResult == 0){
			if(this.hasEndTime() && o.hasEndTime()) {
				compareResult = this.getEndTime().compareTo(o.getEndTime());
			}else if(!this.hasEndTime() && !o.hasEndTime()){
				compareResult = 0;
			}else if(this.hasEndTime()){
				compareResult = -1;
			}else if(o.hasEndTime()){
				compareResult = 1;
			}
		}
		//if end times the same, compare tasks
		if(compareResult == 0){
			compareResult = this.task.compareTo(o.getTask());
		}

		return compareResult;
	}
}
