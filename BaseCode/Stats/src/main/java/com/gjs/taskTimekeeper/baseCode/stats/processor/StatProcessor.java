package com.gjs.taskTimekeeper.baseCode.stats.processor;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.stats.results.Results;

import java.util.Optional;

/**
 * Main abstract class for dealing with creating statistics.
 *
 * TODO:: update this to be threadsafe
 *
 * @param <T> The type of result this processor produces
 */
public abstract class StatProcessor<T extends Results> {
    private T results;

    protected void setResults(T results){
        this.results = results;
    }

    /**
     * Does the statistical processing for the time manager.
     *
     * Populates/ updates the {@link #results} variable, and returns the results.
     * @param manager The time manager to get statistics about
     * @return The results from the processing. The last run also available through {@link #getResults()}
     */
    public abstract T process(TimeManager manager) throws StatProcessingException;

    /**
     * Returns the results. Empty if not run, or an exception occurred in processing.
     *
     * @return The results of processing on the time manager.
     */
    public Optional<T> getResults(){
        if(results == null){
            return Optional.empty();
        }
        return Optional.of(this.results);
    }

    /**
     * Resets the results held.
     */
    public void resetResults(){
        this.results = null;
    }
}
