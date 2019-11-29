package com.gjs.taskTimekeeper.baseCode.stats.processor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import org.junit.Before;
import org.junit.Test;

public abstract class StatProcessorTest<T extends StatProcessor> {
    private static TimeManager TEST_MANAGER = new TimeManager();

    static {
        // TODO:: setup test manager
    }

    public TimeManager getTestManager() {
        return TEST_MANAGER.clone();
    }

    protected T processor;

    @Before
    public abstract void setupProcessor();

    @Test
    public void getResultsTest() {
        assertFalse(this.processor.getResults().isPresent());

        this.processor.process(getTestManager());

        assertTrue(this.processor.getResults().isPresent());
    }

    @Test
    public void resetResultsTest() {
        this.processor.process(getTestManager());

        this.processor.resetResults();

        assertFalse(this.processor.getResults().isPresent());
    }
}
