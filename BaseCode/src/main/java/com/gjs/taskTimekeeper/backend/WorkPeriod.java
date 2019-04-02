package com.gjs.taskTimekeeper.backend;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Defines a period of work, or a set of {@link Timespan}s.
 * Has its own attributes for the user to set.
 */
public class WorkPeriod implements Comparable<WorkPeriod> {

	/** The timespans in this period. */
	private SortedSet<Timespan> timespans = new TreeSet<>();
	/** The attributes held by the period */
	private Map<String, String> attributes = new HashMap<>();

	/**
	 * Base constructor.
	 */
	public WorkPeriod(){

	}

	/**
	 * Constructor to set the timespans.
	 * @param timespans The timespans to set.
	 * @throws NullPointerException If the list given is null or an element in the list is null.
	 */
	public WorkPeriod(Collection<Timespan> timespans) throws NullPointerException {
		this();
		if(timespans instanceof SortedSet){
			this.setTimespans((SortedSet)timespans);
		}else {
			this.setTimespans(timespans);
		}
	}

	/**
	 * Constructor to set the timespans and attributes.
	 * @param timespans The timespans to set.
	 * @param attributes The attributes to set.
	 * @throws NullPointerException If the list given is null or an element in the list is null, or if the attributes are null.
	 */
	public WorkPeriod(Collection<Timespan> timespans, Map<String, String> attributes) throws NullPointerException {
		this(timespans);
		this.setAttributes(attributes);
	}

	/**
	 * Adds a timespan to the work period.
	 *
	 * If timespan.equals({any in set}), it will not be kept. The internal set object will consider it the same as another.
	 *  Same for timespan.compareTo({any in set}) == 0
	 *
	 *
	 * @param timespan The timespan to add
	 * @return The result from adding the timespan to the set. Presumably if it was actually added or not.
	 * @throws NullPointerException If the timespan given is null.
	 */
	public boolean addTimespan(Timespan timespan) throws NullPointerException{
		if(timespan == null){
			throw new NullPointerException("Timespan cannot be null.");
		}
		return this.timespans.add(timespan);
	}

	/**
	 * Gets the list of timespans held.
	 * TODO:: prevent tampering, adding nulls. Defensive copy?
	 * @return The list of timespans.
	 */
	public SortedSet<Timespan> getTimespans() {
		return timespans;
	}

	/**
	 * Sets the list of timespans.
	 * @param timespans The new list of timespans
	 * @return This object
	 * @throws NullPointerException If the ist given is null or any of the contents of the list is null.
	 */
	public WorkPeriod setTimespans(SortedSet<Timespan> timespans) throws NullPointerException {
		if(timespans == null){
			throw new NullPointerException("Timespans cannot ne null.");
		}

		if(!timespans.isEmpty()) {
			boolean containsNullValues = false;

			try {
				containsNullValues = timespans.contains(null);
			}catch (NullPointerException e){

			}
			if(containsNullValues){
				throw new NullPointerException("Cannot hold null timespans.");
			}
		}

		this.timespans = timespans;
		return this;
	}

	/**
	 * Sets the list of timespans with a generic collection.
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
	 * @return the attributes held by this period.
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * Sets the attributes held by this period.
	 * @param attributes The new attributes to be held.
	 * @return This period object.
	 * @throws NullPointerException If the attributes given are null.
	 */
	public WorkPeriod setAttributes(Map<String, String> attributes) throws NullPointerException {
		if(attributes == null){
			throw new NullPointerException("Attributes cannot be null.");
		}
		this.attributes = attributes;
		return this;
	}

	/**
	 * Gets the total time defined by all completed timespans.
	 * @return the total time defined by all completed timespans.
	 */
	public Duration getTotalTime(){
		Duration duration = Duration.ZERO;

		for(Timespan span : this.getTimespans()){
			try{
				duration = duration.plus(span.getDuration());
			}catch (IllegalStateException e){
				//don't need to do anything
			}
		}
		return duration;
	}

	/**
	 * Gets the starting time of this period, as defined by the earliest timespan.
	 * @return the starting time of this period
	 * @throws IllegalStateException If there are no timespans or the first timespan does not specify a start time.
	 */
	public LocalDateTime getStart() throws IllegalStateException {
		if(this.getTimespans().isEmpty()){
			throw new IllegalStateException("No timespans added; cannot determine start.");
		}

		LocalDateTime dateTime = this.getTimespans().first().getStartTime();
		if(dateTime == null){
			throw new IllegalStateException("No timespans exist that have a start datetime; Cannot determine start.");
		}
		return dateTime;
	}

	/**
	 * Gets the ending time of this period, as defined by the latest timespan.
	 * @return the ending time of this period, as defined by the latest timespan.
	 * @throws IllegalStateException If there are no timespans, or none exist with an ending datetime.
	 */
	public LocalDateTime getEnd() throws IllegalStateException {
		if(this.getTimespans().isEmpty()){
			throw new IllegalStateException("No timespans added; cannot determine end.");
		}
		LocalDateTime dateTime = null;

		{
			Timespan spans[] = (Timespan[]) this.getTimespans().toArray();

			for(int i = spans.length - 1; i > -1; i--){
				dateTime = spans[i].getEndTime();
				if(dateTime != null){
					break;
				}
			}
		}

		if(dateTime == null){
			throw new IllegalStateException("No timespans exist that have an end datetime. Ca");
		}

		return dateTime;
	}

	/**
	 * Gets a list of all spans that are yet to be completed.
	 * @return a Collection of all spans that are yet to be completed.
	 */
	public Collection<Timespan> getUnfinishedTimespans(){
		List<Timespan> spans = new LinkedList<>();

		for(Timespan span : this.getTimespans()){
			if(!span.isComplete()){
				spans.add(span);
			}
		}

		return spans;
	}

	/**
	 * Determines if the work period has any timespans uncompleted.
	 * @return if the work period has any timespans uncompleted.
	 */
	public boolean hasUnfinishedTimespans(){
		for(Timespan span : this.getTimespans()){
			if(!span.isComplete()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if this period contains the timespan given.
	 * @param timespan The timespan given to test if this holds it.
	 * @return if this period contains the timespan given.
	 */
	public boolean contains(Timespan timespan){
		return this.getTimespans().contains(timespan);
	}

	/**
	 * Gets all timespans with the given task.
	 * @param task The task to look for
	 * @return all timespans with the given task.
	 */
	public Collection<Timespan> getTimespansWith(Task task){
		return this.getTimespans().stream().filter((Timespan span)->{
			return span.getTask().equals(task);
		}).collect(Collectors.toList());
	}

	/**
	 * Determines if this period has timespans with the given task.
	 * @param task The task given
	 * @return if this period has timespans with the given task.
	 */
	public boolean hasTimespansWith(Task task){
		for(Timespan span : this.getTimespans()){
			if(span.getTask().equals(task)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns:
	 *   -1 if this starts before o
	 *   -1 if o has no start
	 *    0 if this starts at the same time as o
	 *    0 if both this and o have no start time
	 *    1 if this starts after o
	 *    1 if this has no start
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(WorkPeriod o) {
		if(o == null){
			throw new NullPointerException("Cannot compare to null.");
		}
		LocalDateTime thisStart = null;
		LocalDateTime oStart = null;
		try {
			thisStart = this.getStart();
		}catch (IllegalStateException e){
			//nothing to do
		}
		try {
			oStart = o.getStart();
		}catch (IllegalStateException e){
			//nothing to do
		}

		if(thisStart == null && oStart == null){
			return 0;
		}
		if(thisStart == null){
			return 1;
		}
		if(oStart == null){
			return -1;
		}
		return thisStart.compareTo(oStart);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof WorkPeriod)) return false;
		WorkPeriod period = (WorkPeriod) o;
		return getTimespans().equals(period.getTimespans()) &&
				getAttributes().equals(period.getAttributes());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTimespans(), getAttributes());
	}
}
