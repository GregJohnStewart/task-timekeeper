package com.gjs.taskTimekeeper.baseCode.stats.stats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Describes a percentage based result.
 *
 * <p>TODO:: make threadsafe? doublecheck
 *
 * @param <T> The type of object to use for the percent.
 */
@Data
public class PercentStats <T> extends Stats {
	private Map<T, Double> percentages = new ConcurrentHashMap<>();
	private Map<T, String> valueStrings = new ConcurrentHashMap<>();
	private Map<T, Number> values = new ConcurrentHashMap<>();
	
	/**
	 * Base constructor. No values added.
	 */
	public PercentStats() {
	}
	
	/**
	 * Constructor to set the initial values.
	 *
	 * @param values The values to set initially.
	 * @throws NullPointerException If the map of values given is null.
	 */
	public PercentStats(Map<T, Number> values, Map<T, String> strings)
		throws NullPointerException {
		this.setValues(values, strings);
	}
	
	public PercentStats(Map<T, Number> values) throws NullPointerException {
		this.setValues(values);
	}
	
	/**
	 * Recalculates percentages. Use whenever {@link #values} is modified.
	 */
	private synchronized void recalculatePercentages() {
		this.percentages = new ConcurrentHashMap<>();
		
		double total = 0.0;
		for(Map.Entry<T, Number> entry : this.values.entrySet()) {
			total += entry.getValue().doubleValue();
		}
		for(Map.Entry<T, Number> entry : this.values.entrySet()) {
			this.percentages.put(entry.getKey(), (entry.getValue().doubleValue() / total) * 100);
		}
	}
	
	/**
	 * Sets a value to be weighted against the rest of the values. Also sets an extra display value.
	 *
	 * @param obj     The object to set a value for.
	 * @param value   The value to set.
	 * @param display The string used to display the value given (separate from the percentage)
	 * @return The previous percentage at the object. Null if no previous percentage held.
	 * @throws NullPointerException if the object or value given was null.
	 */
	public synchronized Number setValue(T obj, Number value, String display)
		throws NullPointerException {
		Number output = this.setValue(obj, value);
		this.valueStrings.put(obj, display);
		return output;
	}
	
	/**
	 * Sets a value to be weighted against the rest of the values.
	 *
	 * @param obj   The object to set a value for.
	 * @param value The value to set.
	 * @return The previous percentage at the object. Null if no previous percentage held.
	 * @throws NullPointerException if the object or value given was null.
	 */
	public synchronized Number setValue(T obj, Number value) throws NullPointerException {
		if(obj == null) {
			throw new NullPointerException("Cannot set a null object.");
		}
		if(value == null) {
			throw new NullPointerException("Cannot set a null value.");
		}
		
		Double output = this.percentages.get(obj);
		
		this.values.put(obj, value);
		this.recalculatePercentages();
		
		return output;
	}
	
	/**
	 * Sets the values to the ones given. Clears out existing values and uses the ones given.
	 *
	 * @param values The values to set.
	 * @throws NullPointerException If the map of values given is null.
	 */
	public synchronized void setValues(Map<T, Number> values, Map<T, String> valueStrings)
		throws NullPointerException {
		this.setValues(values);
		this.valueStrings = new ConcurrentHashMap<>(valueStrings);
		this.recalculatePercentages();
	}
	
	public synchronized void setValues(Map<T, Number> values) throws NullPointerException {
		if(values == null) {
			throw new NullPointerException("Values given cannot be null.");
		}
		this.values = new ConcurrentHashMap<>(values);
		this.recalculatePercentages();
	}
	
	/**
	 * Adds or overrides the values given to the ones already held.
	 *
	 * @param values The values to add or override with.
	 * @throws NullPointerException If the map of values given is null.
	 */
	public synchronized void addOrOverrideValues(Map<T, Number> values, Map<T, String> valueStrings)
		throws NullPointerException {
		if(valueStrings == null) {
			throw new NullPointerException("Value strings given cannot be null.");
		}
		this.addOrOverrideValues(values);
		for(Map.Entry<T, String> entry : valueStrings.entrySet()) {
			this.valueStrings.put(entry.getKey(), entry.getValue());
		}
	}
	
	public synchronized void addOrOverrideValues(Map<T, Number> values)
		throws NullPointerException {
		if(values == null) {
			throw new NullPointerException("Values given cannot be null.");
		}
		for(Map.Entry<T, Number> entry : values.entrySet()) {
			this.values.put(entry.getKey(), entry.getValue());
		}
		this.recalculatePercentages();
	}
	
	/**
	 * Removes the value from the set.
	 *
	 * @param obj The object to remove the value from.
	 * @return The previously held value. Null if was not set.
	 */
	public synchronized Number remove(T obj) {
		Number output = this.values.remove(obj);
		
		if(output != null) {
			this.valueStrings.remove(obj);
			this.recalculatePercentages();
		}
		
		return output;
	}
	
	/**
	 * Retrieves the percentages calculated by this object.
	 *
	 * @return The percentages based on the values set.
	 */
	public synchronized Map<T, Double> getPercentages() {
		return new ConcurrentHashMap<>(this.percentages);
	}
	
	/**
	 * Gets the values set.
	 *
	 * @return The values set in this object.
	 */
	public synchronized Map<T, Number> getValues() {
		return new ConcurrentHashMap<>(this.values);
	}
	
	/**
	 * Gets the values set.
	 *
	 * @return The values set in this object.
	 */
	public synchronized Map<T, String> getValueStrings() {
		return new ConcurrentHashMap<>(this.valueStrings);
	}
	
	/**
	 * Gets the number of percentages held.
	 *
	 * @return The number of percentages held.
	 */
	public synchronized int getNumEntries() {
		return this.percentages.size();
	}
	
	/**
	 * Gets the set of objects there are percentages for.
	 *
	 * @return The set of objects there are percentages for.
	 */
	@JsonIgnore
	public synchronized Set<T> getObjects() {
		return this.percentages.keySet();
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}
		PercentStats<?> that = (PercentStats<?>)o;
		return getPercentages().equals(that.getPercentages()) && values.equals(that.values);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getPercentages(), values);
	}
}
