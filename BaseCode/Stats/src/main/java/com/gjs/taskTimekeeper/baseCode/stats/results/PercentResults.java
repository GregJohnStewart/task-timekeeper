package com.gjs.taskTimekeeper.baseCode.stats.results;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Describes a percentage based result.
 *
 * <p>TODO:: make threadsafe? doublecheck
 *
 * @param <T> The type of object to use for the percent.
 */
public class PercentResults<T> extends Results {
    private ConcurrentMap<T, Double> percentages = new ConcurrentHashMap<>();
    private final ConcurrentMap<T, Double> values = new ConcurrentHashMap<>();

    /** Recalculates percentages. Use whenever {@link #values} is modified. */
    private synchronized void recalculatePercentages() {
        this.percentages = new ConcurrentHashMap<>();

        Double total = 0.0;
        for (Map.Entry<T, Double> entry : this.values.entrySet()) {
            total += entry.getValue();
        }
        for (Map.Entry<T, Double> entry : this.values.entrySet()) {
            this.percentages.put(entry.getKey(), (entry.getValue() / total) * 100);
        }
    }

    /**
     * Sets a value to be weighted against the rest of the values.
     *
     * @param obj The object to set a value for.
     * @param value The value to set.
     * @return The previous percentage at the object. Null if no previous percentage held.
     * @throws NullPointerException if the object or value given was null.
     */
    public synchronized Double setValue(T obj, Double value) throws NullPointerException {
        if (obj == null) {
            throw new NullPointerException("Cannot set a null object.");
        }
        if (value == null) {
            throw new NullPointerException("Cannot set a null value.");
        }

        Double output = this.percentages.get(obj);

        this.values.put(obj, value);
        this.recalculatePercentages();

        return output;
    }

    /**
     * Removes the value from the set.
     *
     * @param obj The object to remove the value from.
     * @return The previously held value. Null if was not set.
     */
    public synchronized Double remove(T obj) {
        Double output = this.values.remove(obj);

        if (output != null) {
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
    public synchronized Set<T> getObjects() {
        return this.percentages.keySet();
    }
}
