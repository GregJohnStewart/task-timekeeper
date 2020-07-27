package com.gjs.taskTimekeeper.baseCode.core.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Defines a period of work, or a set of {@link Timespan}s. Has its own attributes for the user to
 * set.
 * <p>
 * TODO:: investigate using lombok
 */
@JsonIgnoreProperties(value = {"durationString", "startString", "endString", "completed"}, allowGetters = true)
public class WorkPeriod extends KeeperObject implements Comparable<WorkPeriod> {
	
	/**
	 * The timespans in this period.
	 */
	private SortedSet<Timespan> timespans = new TreeSet<>();
	/**
	 * The attributes held by the period
	 */
	private Map<String, String> attributes = new HashMap<>();
	
	/**
	 * Base constructor.
	 */
	public WorkPeriod() {
	}
	
	/**
	 * Constructor to set the timespans.
	 *
	 * @param timespans The timespans to set.
	 * @throws NullPointerException If the list given is null or an element in the list is null.
	 */
	public WorkPeriod(Collection<Timespan> timespans) throws NullPointerException {
		this();
		if(timespans instanceof SortedSet) {
			this.setTimespans(timespans);
		} else {
			this.setTimespans(timespans);
		}
	}
	
	/**
	 * Constructor to set the timespans and attributes.
	 *
	 * @param timespans  The timespans to set.
	 * @param attributes The attributes to set.
	 * @throws NullPointerException If the list given is null or an element in the list is null, or
	 *                              if the attributes are null.
	 */
	public WorkPeriod(Collection<Timespan> timespans, Map<String, String> attributes)
		throws NullPointerException {
		this(timespans);
		this.setAttributes(attributes);
	}
	
	/**
	 * Adds a timespan to the work period.
	 *
	 * <p>If timespan.equals({any in set}), it will not be kept. The internal set object will
	 * consider it the same as another. Same for timespan.compareTo({any in set}) == 0
	 *
	 * @param timespan The timespan to add
	 * @return The result from adding the timespan to the set. Presumably if it was actually added
	 * 	or not.
	 * @throws NullPointerException If the timespan given is null.
	 */
	public boolean addTimespan(Timespan timespan) throws NullPointerException {
		if(timespan == null) {
			throw new NullPointerException("Timespan cannot be null.");
		}
		return this.timespans.add(timespan);
	}
	
	/**
	 * Adds a number of timespans to the period.
	 *
	 * @param timespans The timespans to add
	 * @return This object
	 * @throws NullPointerException If any of the given timespans are null
	 */
	public WorkPeriod addTimespans(Timespan... timespans) throws NullPointerException {
		for(Timespan span : timespans) {
			this.addTimespan(span);
		}
		return this;
	}
	
	/**
	 * Gets the list of timespans held.
	 *
	 * <p>Do not add nulls to the list.
	 *
	 * @return The list of timespans.
	 */
	@JsonProperty("timespans")
	public SortedSet<Timespan> getTimespans() {
		return timespans;
	}
	
	/**
	 * Gets the number of timespans held.
	 *
	 * @return The number of timespans held.
	 */
	@JsonIgnore
	public int getNumTimespans() {
		return this.getTimespans().size();
	}
	
	/**
	 * Sets the list of timespans.
	 *
	 * @param timespans The new list of timespans
	 * @return This object
	 * @throws NullPointerException If the ist given is null or any of the contents of the list is
	 *                              null.
	 */
	@JsonIgnore
	public WorkPeriod setTimespans(SortedSet<Timespan> timespans) throws NullPointerException {
		if(timespans == null) {
			throw new NullPointerException("Timespans cannot ne null.");
		}
		
		if(!timespans.isEmpty()) {
			boolean containsNullValues = false;
			
			try {
				containsNullValues = timespans.contains(null);
			} catch(NullPointerException e) {
				// nothing to do
			}
			if(containsNullValues) {
				throw new NullPointerException("Cannot hold null timespans.");
			}
		}
		
		this.timespans = timespans;
		return this;
	}
	
	/**
	 * Sets the list of timespans with a generic collection.
	 *
	 * @param timespans The timespans to set.
	 * @return This period object.
	 * @throws NullPointerException If the timespan collection given is null or any values are null.
	 */
	public WorkPeriod setTimespans(Collection<Timespan> timespans) throws NullPointerException {
		this.setTimespans(new TreeSet<>(timespans));
		return this;
	}
	
	/**
	 * Gets the attributes held by this period.
	 *
	 * @return the attributes held by this period.
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	/**
	 * Sets the attributes held by this period.
	 *
	 * @param attributes The new attributes to be held.
	 * @return This period object.
	 * @throws NullPointerException If the attributes given are null.
	 */
	public WorkPeriod setAttributes(Map<String, String> attributes) throws NullPointerException {
		if(attributes == null) {
			throw new NullPointerException("Attributes cannot be null.");
		}
		this.attributes = attributes;
		return this;
	}
	
	/**
	 * Gets the total time defined by all completed timespans.
	 *
	 * @return the total time defined by all completed timespans.
	 */
	@JsonIgnore
	public Duration getTotalTime() {
		return this.getTotalTimeWith(null);
	}
	
	/**
	 * Gets the total time spent on a particular task.
	 *
	 * @param task The task to get the time spent on. Null if you want time spent on all tasks.
	 * @return the total time defined by all completed timespans with the task given.
	 */
	@JsonIgnore
	public Duration getTotalTimeWith(Name task) {
		Duration duration = Duration.ZERO;
		
		for(Timespan span : this.getTimespans()) {
			if(task != null && !span.getTaskName().equals(task)) {
				continue;
			}
			try {
				duration = duration.plus(span.getDuration());
			} catch(IllegalStateException e) {
				// don't need to do anything
			}
		}
		return duration;
	}
	
	/**
	 * Gets the starting time of this period, as defined by the earliest timespan.
	 *
	 * @return the starting time of this period
	 */
	@JsonIgnore
	public LocalDateTime getStart() {
		if(this.getTimespans().isEmpty()) {
			return null;
		}
		
		return this.getTimespans().first().getStartTime();
	}
	
	/**
	 * Gets the ending time of this period, as defined by the latest timespan.
	 *
	 * @return the ending time of this period, as defined by the latest timespan.
	 * @throws IllegalStateException If there are no timespans, or none exist with an ending
	 *                               datetime.
	 */
	@JsonIgnore
	public LocalDateTime getEnd() throws IllegalStateException {
		if(this.getTimespans().isEmpty()) {
			return null;
		}
		LocalDateTime dateTime = null;
		
		{
			Timespan[] spans = new Timespan[this.getTimespans().size()];
			this.getTimespans().toArray(spans);
			
			for(int i = spans.length - 1; i > -1; i--) {
				dateTime = spans[i].getEndTime();
				if(dateTime != null) {
					break;
				}
			}
		}
		
		return dateTime;
	}
	
	/**
	 * Determines if this span has a start time set.
	 *
	 * @return if this span has a start time set.
	 */
	public boolean hasStartTime() {
		return this.getStart() != null;
	}
	
	/**
	 * Determines if this span has a end time set.
	 *
	 * @return if this span has a end time set.
	 */
	public boolean hasEndTime() {
		return this.getEnd() != null;
	}
	
	/**
	 * Gets a list of all spans that are yet to be completed.
	 *
	 * @return a Collection of all spans that are yet to be completed.
	 */
	@JsonIgnore
	public Collection<Timespan> getUnfinishedTimespans() {
		List<Timespan> spans = new LinkedList<>();
		
		for(Timespan span : this.getTimespans()) {
			if(!span.isComplete()) {
				spans.add(span);
			}
		}
		
		return spans;
	}
	
	/**
	 * Determines if the work period has any timespans uncompleted.
	 *
	 * @return if the work period has any timespans uncompleted.
	 */
	@JsonIgnore
	public boolean hasUnfinishedTimespans() {
		for(Timespan span : this.getTimespans()) {
			if(!span.isComplete()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isCompleted() {
		return !this.isUnCompleted();
	}
	
	/**
	 * Determines if this work period is unfinished.
	 *
	 * @return if this work period is unfinished.
	 */
	@JsonIgnore
	public boolean isUnCompleted() {
		return this.getTimespans().isEmpty() || this.hasUnfinishedTimespans();
	}
	
	/**
	 * Determines if this period contains the timespan given.
	 *
	 * @param timespan The timespan given to test if this holds it.
	 * @return if this period contains the timespan given.
	 */
	public boolean contains(Timespan timespan) {
		return this.getTimespans().contains(timespan);
	}
	
	/**
	 * Gets all timespans with the given task.
	 *
	 * @param task The task to look for
	 * @return all timespans with the given task.
	 */
	public Collection<Timespan> getTimespansWith(Task task) {
		return this.getTimespans().stream()
				   .filter(
					   (Timespan span)->{
						   return span.getTaskName().equals(task.getName());
					   })
				   .collect(Collectors.toList());
	}
	
	/**
	 * Determines if this period has timespans with the given task.
	 *
	 * @param task The task given
	 * @return if this period has timespans with the given task.
	 */
	public boolean hasTimespansWith(Task task) {
		for(Timespan span : this.getTimespans()) {
			if(span.getTaskName().equals(task.getName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets a list of tasks that are held by the Timespans in this period.
	 *
	 * @return A list of tasks that are held by the Timespans in this period.
	 */
	@JsonIgnore
	public SortedSet<Name> getTaskNames() {
		SortedSet<Name> tasks = new TreeSet<>();
		
		for(Timespan span : this.getTimespans()) {
			tasks.add(span.getTaskName());
		}
		
		return tasks;
	}
	
	@JsonIgnore
	public Duration getDuration() {
		Duration duration = Duration.ZERO;
		
		for(Timespan curSpan : this.getTimespans()) {
			duration = duration.plus(curSpan.getDuration());
		}
		
		return duration;
	}
	
	public String getStartString() {
		return (this.hasStartTime() ? TimeParser.toOutputString(this.getStart()) : "");
	}
	
	public String getEndString() {
		return (this.hasEndTime() ? TimeParser.toOutputString(this.getEnd()) : "");
	}
	
	public String getDurationString() {
		return TimeParser.toDurationString(this.getDuration());
	}
	
	/**
	 * Compares this work period to the datetime given.
	 *
	 * <p>TODO:: test
	 *
	 * @param dt The datetime to compare to.
	 * @return 0 if the period encompasses the datetime. -1 if it is before, 1 if after.
	 */
	@JsonIgnore
	public int compareTo(LocalDateTime dt) {
		if(this.getStart() == null && this.getEnd() == null) {
			return 0;
		} else if(this.getStart() == null) {
			if(this.getEnd().isBefore(dt)) {
				return -1;
			} else if(this.getEnd().isAfter(dt)) {
				return 1;
			}
			return 0;
		} else if(this.getEnd() == null) {
			if(this.getStart().isBefore(dt)) {
				return -1;
			} else if(this.getStart().isAfter(dt)) {
				return 1;
			}
			return 0;
		}
		
		if(this.getStart().isAfter(dt)) {
			return 1;
		} else if(this.getEnd().isBefore(dt)) {
			return -1;
		}
		return 0;
	}
	
	/**
	 * Returns: -1 if this starts before o -1 if o has no start 0 if this starts at the same time as
	 * o 0 if both this and o have no start time 1 if this starts after o 1 if this has no start
	 *
	 * @param o The work period to compare to
	 * @return
	 */
	@Override
	public int compareTo(WorkPeriod o) {
		if(o == null) {
			throw new NullPointerException("Cannot compare to null.");
		}
		LocalDateTime thisStart = this.getStart();
		LocalDateTime oStart = o.getStart();
		
		if(thisStart == null && oStart == null) {
			return 0;
		}
		if(thisStart == null) {
			return 1;
		}
		if(oStart == null) {
			return -1;
		}
		return thisStart.compareTo(oStart);
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof WorkPeriod)) {
			return false;
		}
		WorkPeriod period = (WorkPeriod)o;
		return getTimespans().equals(period.getTimespans())
			&& getAttributes().equals(period.getAttributes());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getTimespans(), getAttributes());
	}
	
	@Override
	public WorkPeriod clone() {
		TreeSet<Timespan> newSpans = new TreeSet<>();
		for(Timespan span : this.timespans) {
			newSpans.add(span.clone());
		}
		return new WorkPeriod(newSpans, new HashMap<>(this.attributes));
	}
}
